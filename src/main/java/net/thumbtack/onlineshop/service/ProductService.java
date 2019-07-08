package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Category;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.database.models.ProductCategory;
import net.thumbtack.onlineshop.database.models.Session;
import net.thumbtack.onlineshop.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class ProductService {

    private ProductDao productDao;
    private SessionDao sessionDao;
    private CategoryDao categoryDao;

    public enum SortOrder {
        PRODUCT, CATEGORY
    }

    @Autowired
    public ProductService(ProductDao productDao, SessionDao sessionDao, CategoryDao categoryDao) {
        this.productDao = productDao;
        this.sessionDao = sessionDao;
        this.categoryDao = categoryDao;
    }

    /**
     * Добавляет товар в бд
     * @param sessionId сессия админа
     * @param productDto информация о товаре
     * @return информация о зарегестрированном в БД товаре
     * @throws ServiceException
     */
    public Product add(String sessionId, ProductDto productDto) throws ServiceException {
        isAdmin(sessionId);

        // Добавляем товар в БД
        Product product = new Product(
                productDto.getName(),
                productDto.getCount(),
                productDto.getPrice());

        // Отдельно добавляем список категорий
        if (productDto.getCategories() != null) {

            List<Category> newCategories = new ArrayList<>();

            for (int categoryId : productDto.getCategories()) {
                Category category = categoryDao.get(categoryId);
                if (category == null)
                    throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND, "categories");
                newCategories.add(category);
            }

            // И если проблем не было, значит можем обновить объект
            productDao.insert(product);

            // Добавляем новые категории
            for (Category category : newCategories)
                productDao.insertCategory(new ProductCategory(product, category));

            return product;
        } else {
            productDao.insert(product);
            return product;
        }
    }

    /**
     * Изменяет товар в БД
     * @param sessionId сессия админа
     * @param productDto новая информация о товаре
     * @param productId id товара
     * @return информация об изменённом в БД товаре
     * @throws ServiceException
     */
    public Product edit(String sessionId, ProductDto productDto, long productId) throws ServiceException {
        isAdmin(sessionId);

        Product product = productDao.get(productId);

        if (product == null)
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, "id");

        product.setName(productDto.getName());
        product.setCount(productDto.getCount());
        product.setPrice(productDto.getPrice());

        if (productDto.getCategories() != null) {

            // Сохраняем список старых категорий товара
            List<ProductCategory> oldCategories = productDao.getCategories(productId);

            // Формируем новый список категорий
            List<ProductCategory> newCategories = new ArrayList<>();

            // Добавляем новые категории
            for (long categoryId : productDto.getCategories()) {
                Category category = categoryDao.get(categoryId);
                if (category == null)
                    throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND, "categories");

                newCategories.add(new ProductCategory(product, category));
            }

            // Если никаких ошибок не было, то удаляем старые
            for (ProductCategory category : oldCategories)
                productDao.deleteCategory(category);

            // И добавляем новые
            for (ProductCategory category : newCategories)
                productDao.insertCategory(category);

        }

        // И только если с категориями не было проблем,
        // то обновляем товар
        productDao.update(product);
        return product;
    }

    /**
     * Удаляет товар из БД
     * @param sessionId сессия админа
     * @param id id товара
     * @throws ServiceException
     */
    public void delete(String sessionId, long id) throws ServiceException {
        isAdmin(sessionId);

        Product product = productDao.get(id);

        if (product == null)
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND);

        // Удаляем категории товара из БД
        List<ProductCategory> categories = productDao.getCategories(id);
        for (ProductCategory category : categories)
            productDao.deleteCategory(category);

        // Удаляем в конце товар
        productDao.delete(product);
    }

    /**
     * Получает информацию о товаре
     * @param sessionId сессия админа
     * @param id id товара
     * @return информация о товаре из БД
     * @throws ServiceException
     */
    public Product get(String sessionId, long id) throws ServiceException {
        isLogin(sessionId);

        Product product = productDao.get(id);

        if (product == null)
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND);

        return product;
    }

    /**
     * Получает список категорий товара
     * @param sessionId сессия админа
     * @param productId id товара
     * @return список категорий для данного товара
     * @throws ServiceException
     */
    public List<ProductCategory> getCategories(String sessionId, long productId) throws ServiceException {
        isLogin(sessionId);

        return productDao.getCategories(productId);
    }

    public List<ProductCategory> getAll(String sessionId, List<Integer> categories, SortOrder order) throws ServiceException {
        isLogin(sessionId);

        return null;
    }

    private void isAdmin(String sessionId) throws ServiceException {
        Session session = sessionDao.get(sessionId);

        if (session == null)
            throw new ServiceException(ServiceException.ErrorCode.NOT_LOGIN);

        if (!session.getAccount().isAdmin())
            throw new ServiceException(ServiceException.ErrorCode.NOT_ADMIN);
    }

    private void isLogin(String sessionId) throws ServiceException {
        Session session = sessionDao.get(sessionId);

        if (session == null)
            throw new ServiceException(ServiceException.ErrorCode.NOT_LOGIN);
    }
}

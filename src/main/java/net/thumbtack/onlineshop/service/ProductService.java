package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Category;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.database.models.ProductCategory;
import net.thumbtack.onlineshop.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService extends GeneralService {

    private ProductDao productDao;
    private SessionDao sessionDao;
    private CategoryDao categoryDao;

    public enum SortOrder {
        PRODUCT, CATEGORY
    }

    @Autowired
    public ProductService(ProductDao productDao, SessionDao sessionDao, CategoryDao categoryDao) {
        super(sessionDao);
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
    public ProductDto add(String sessionId, ProductDto productDto) throws ServiceException {
        getAdmin(sessionId);

        // Добавляем товар в БД
        Product product = new Product(
                productDto.getName(),
                productDto.getCount(),
                productDto.getPrice());

        // TODO: А что будет если номера категорий будут повторяться?

        // Отдельно добавляем список категорий
        if (productDto.getCategories() != null) {

            List<Category> newCategories = new ArrayList<>();

            for (long categoryId : productDto.getCategories()) {
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

        } else {
            productDao.insert(product);
        }

        return new ProductDto(product, productDao.getCategories(product.getId()));
    }

    /**
     * Изменяет товар в БД
     * @param sessionId сессия админа
     * @param productDto новая информация о товаре
     * @param productId id товара
     * @return информация об изменённом в БД товаре
     * @throws ServiceException
     */
    public ProductDto edit(String sessionId, ProductDto productDto, long productId) throws ServiceException {
        getAdmin(sessionId);

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

        return new ProductDto(product, productDao.getCategories(product.getId()));
    }

    /**
     * Удаляет товар из БД
     * @param sessionId сессия админа
     * @param id id товара
     * @throws ServiceException
     */
    public void delete(String sessionId, long id) throws ServiceException {
        getAdmin(sessionId);

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
    public ProductDto get(String sessionId, long id) throws ServiceException {
        getAccount(sessionId);

        Product product = productDao.get(id);

        if (product == null)
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND);

        return new ProductDto(product, productDao.getCategories(product.getId()));
    }

    /**
     * Получает список категорий товара
     * @param sessionId сессия админа
     * @param productId id товара
     * @return список категорий для данного товара
     * @throws ServiceException
     */
    public List<ProductCategory> getCategories(String sessionId, long productId) throws ServiceException {
        // TODO: От этого метода вообще надо избавится
        getAccount(sessionId);

        return productDao.getCategories(productId);
    }

    /**
     * Получает список всех товаров отсортированных и отобранных по необходимым условиям
     * @param sessionId сессия админа
     * @param categories список категорий
     * @param order порядок сортировки
     * @return список товаров
     * @throws ServiceException
     */
    public List<ProductDto> getAll(String sessionId, List<Integer> categories, SortOrder order) throws ServiceException {

        // TODO: Избавится от дубликаций кода и упростить метод

        getAccount(sessionId);

        List<ProductCategory> result = new ArrayList<>();

        if (order == null || order == SortOrder.PRODUCT) {
            // Сортировка товаров по именам

            if (categories == null) {

                // Все товары
                List<Product> products = productDao.getAll();
                products.forEach((p) -> result.add(new ProductCategory(p, null)));

            } else if (categories.isEmpty()) {

                // Все товары без категорий
                List<Product> products = productDao.getAllWithoutCategory();
                products.forEach((p) -> result.add(new ProductCategory(p, null)));

            } else {

                // Все товары которые содержат данные категории
                List<ProductCategory> products = productDao.getAllWithCategory();
                Set<Product> resultSet = new HashSet<>();

                for (int category : categories) {
                    for (ProductCategory product : products) {
                        if (product.getCategory().getId() == category)
                            resultSet.add(product.getProduct());
                    }
                }

                // Теперь переносим всё это в конечный результат
                resultSet.forEach((p) -> result.add(new ProductCategory(p, null)));
            }

            // Сортируем по именам товаров
            result.sort(Comparator.comparing((ProductCategory left) -> left.getProduct().getName()));

        } else if (order == SortOrder.CATEGORY) {
            // Сортировка по именам категорий

            if (categories == null) {

                // Сначала добавляем в начало списка все товары без категорий
                List<Product> products = productDao.getAllWithoutCategory();
                products.forEach((p) -> result.add(new ProductCategory(p, null)));
                // И сортируем по именам товаров
                result.sort(Comparator.comparing((ProductCategory left) -> left.getProduct().getName()));

                // Теперь получаем список товаров c категориями
                List<ProductCategory> temp = productDao.getAllWithCategory();
                // И сортируем по именам категорий (а внутри категорий, по именам товаров)
                temp.sort(
                        Comparator.comparing(
                                (ProductCategory left) -> left.getCategory().getName() + left.getProduct().getName()
                        )
                );
                result.addAll(temp);

            } else if (categories.isEmpty()) {

                // Все товары без категорий
                // никаих сортировок здесь не проводим
                List<Product> products = productDao.getAllWithoutCategory();
                products.forEach((p) -> result.add(new ProductCategory(p, null)));

            } else {

                // Получаем список товаров с категориями
                List<ProductCategory> products = productDao.getAllWithCategory();
                for (int category : categories) {
                    for (ProductCategory product : products)
                        if (product.getCategory().getId() == category)
                            result.add(product);
                }

                // Теперь сортируем по именам категорий
                result.sort(
                        Comparator.comparing(
                                (ProductCategory left) -> left.getCategory().getName() + left.getProduct().getName()
                        )
                );
            }

        }

        // Теперь создаём список готовых DTO
        List<ProductDto> response = new ArrayList<>();

        /*
        Код может показаться слегка сложноватым, но суть проста.

        Иногда для товара нужно получить полный список категорий, иногда товар
        в списке должен содержать только упоминание одной категории (если к примеру
        мы используем сортировку по категориям (читай ТЗ)). Мы получаем из БД пары
        ProductCategory. Товары, для которых необходимо получить полный список категорий
        ProductCategory.getCategory() == null. Товары, в которых должна быть только одна категория
        уже содержат её в паре.

        Тривиально.
         */
        for (ProductCategory pc : result) {

            // Если категории нет, то получаем полный список категорий
            // из БД
            if (pc.getCategory() == null)
                response.add(
                        new ProductDto(
                                pc.getProduct(),
                                productDao.getCategories(pc.getProduct().getId())
                        )
                );
            // Если категория есть, то только её и вставляем в конечный объект
            else
                response.add(
                        new ProductDto(
                                pc.getProduct(),
                                Collections.singletonList(pc)
                        )
                );

        }

        return response;
    }

}

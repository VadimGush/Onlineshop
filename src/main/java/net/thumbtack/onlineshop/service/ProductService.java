package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.domain.dao.CategoryDao;
import net.thumbtack.onlineshop.domain.dao.ProductDao;
import net.thumbtack.onlineshop.domain.dao.SessionDao;
import net.thumbtack.onlineshop.domain.models.Category;
import net.thumbtack.onlineshop.domain.models.Product;
import net.thumbtack.onlineshop.domain.models.ProductCategory;
import net.thumbtack.onlineshop.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Сервис для работы с товарами
 */
@Service
public class ProductService extends GeneralService {

    private ProductDao productDao;
    private CategoryDao categoryDao;

    public enum SortOrder {
        PRODUCT, CATEGORY
    }

    @Autowired
    public ProductService(ProductDao productDao, SessionDao sessionDao, CategoryDao categoryDao) {
        super(sessionDao);
        this.productDao = productDao;
        this.categoryDao = categoryDao;
    }

    /**
     * Добавляет товар в бд
     *
     * @param sessionId  сессия админа
     * @param productDto информация о товаре
     * @return информация о зарегестрированном в БД товаре
     */
    public ProductDto add(String sessionId, ProductDto productDto) throws ServiceException {
        getAdmin(sessionId);

        // Добавляем товар в БД
        Product product = new Product(
                productDto.getName(),
                productDto.getCount(),
                productDto.getPrice());

        // Отдельно добавляем список категорий
        if (productDto.getCategories() != null) {

            // Создаём множество, так как категории в запросе могут повторяться
            Set<Long> categories = new HashSet<>(productDto.getCategories());

            List<Category> newCategories = new ArrayList<>();

            for (long categoryId : categories) {
                Category category = categoryDao.get(categoryId);
                if (category == null) {
                    throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND, "categories");
                }
                newCategories.add(category);
            }

            // И если проблем не было, значит можем обновить объект
            productDao.insert(product);

            // Добавляем новые категории
            for (Category category : newCategories) {
                productDao.insertCategory(new ProductCategory(product, category));
            }

        } else {
            productDao.insert(product);
        }

        return new ProductDto(product, productDao.getCategories(product.getId()));
    }

    /**
     * Изменяет товар в БД
     *
     * @param sessionId  сессия админа
     * @param productDto новая информация о товаре
     * @param productId  id товара
     * @return информация об изменённом в БД товаре
     */
    public ProductDto edit(String sessionId, ProductDto productDto, long productId) throws ServiceException {
        getAdmin(sessionId);

        Product product = productDao.get(productId);

        if (product == null) {
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, "id");
        }

        if (productDto.getName() != null && !productDto.getName().isEmpty()) {
            product.setName(productDto.getName());
        }

        if (productDto.getCount() != null) {
            product.setCount(productDto.getCount());
        }

        if (productDto.getPrice() != null) {
            product.setPrice(productDto.getPrice());
        }

        if (productDto.getCategories() != null) {

            // Избавляемся от повторений
            Set<ProductCategory> categories = new HashSet<>();

            // Сформируем новый список категорий
            for (long categoryId : productDto.getCategories()) {
                Category category = categoryDao.get(categoryId);

                if (category == null) {
                    throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND, "categories");
                }

                categories.add(new ProductCategory(product, category));
            }

            // Получаем список старых категорий
            List<ProductCategory> oldCategories = productDao.getCategories(productId);

            for (ProductCategory category : oldCategories) {

                // Если старой категории нет в новом списке, то удаляем
                if (!categories.contains(category)) {
                    productDao.deleteCategory(category);
                } else {
                    // Если она там есть, то просто оставим в БД как есть
                    // и не будем добавлять снова
                    categories.remove(category);
                }
            }

            // Теперь добавим все новые, которые остались
            categories.forEach(category -> productDao.insertCategory(category));

        }

        // И только если с категориями не было проблем,
        // то обновляем товар
        productDao.update(product);

        return new ProductDto(product, productDao.getCategories(product.getId()));
    }

    /**
     * Удаляет товар из БД
     *
     * @param sessionId сессия админа
     * @param id        id товара
     */
    public void delete(String sessionId, long id) throws ServiceException {
        getAdmin(sessionId);

        Product product = productDao.get(id);

        if (product == null) {
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND);
        }

        // Получаем список категорий
        List<ProductCategory> categories = productDao.getCategories(id);

        // Удаляем категории для данного товара
        categories.forEach(category -> productDao.deleteCategory(category));

        // Удаляем в конце товар
        productDao.delete(product);
    }

    /**
     * Получает информацию о товаре
     *
     * @param sessionId сессия админа
     * @param id        id товара
     * @return информация о товаре из БД
     */
    public ProductDto get(String sessionId, long id) throws ServiceException {
        getAccount(sessionId);

        Product product = productDao.get(id);

        if (product == null) {
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND);
        }

        return new ProductDto(product, productDao.getCategories(product.getId()));
    }

    /**
     * Получает список всех товаров отсортированных и отобранных по необходимым условиям
     *
     * @param sessionId  сессия админа
     * @param categories список категорий
     * @param order      порядок сортировки
     * @return список товаров
     */
    public List<ProductDto> getAll(String sessionId, List<Long> categories, SortOrder order) throws ServiceException {

        getAccount(sessionId);

        // Делаем запрос на выборку товаров. Получаем список пар (товар - категория)
        // Если категории в паре нет, то для товара получим полный список категорий
        List<ProductCategory> result = getAllProductsWithSort(categories, order);

        // Теперь создаём список готовых DTO
        List<ProductDto> response = new ArrayList<>();

        for (ProductCategory pc : result) {

            // Если категории нет, то получаем полный список
            if (pc.getCategory() == null) {
                response.add(
                        new ProductDto(
                                pc.getProduct(),
                                productDao.getCategories(pc.getProduct().getId())
                        )
                );
            } else {
                // Если категория есть, то только её и вставляем в конечный объект
                response.add(
                        new ProductDto(
                                pc.getProduct(),
                                Collections.singletonList(pc)
                        )
                );
            }

        }

        return response;
    }

    /**
     * Получаем список всех товаров и ассоциируемых с ними категорий.
     * <br>
     * Для каждого товара мы получаем пару (товар - категория).
     * Для пар с отсутствующей категорией считается что для товара нужно получить полный список категорий.
     *
     * @param categories id категорий, которым должны принадлежать товары
     * @param order      порядок сортировки товаров
     * @return список пар (товар - категория)
     */
    private List<ProductCategory> getAllProductsWithSort(List<Long> categories, SortOrder order) {

        if (order == null || order == SortOrder.PRODUCT) {
            // Сортировка товаров по именам
            return getAllProductsSortedByName(categories);
        } else {
            // Получаем все товары отсортированные по именам категорий
            return getAllProductsSortedByCategory(categories);
        }

    }

    /**
     * Получает список всех товаров, отсортированных по категориям
     *
     * @param categories категории, которым должны принадлежать товары.
     *                   Пустой список - все товары без категорий
     *                   Без списка - все товары
     * @return выборка товаров отсортированных по именам категорий
     */
    private List<ProductCategory> getAllProductsSortedByCategory(List<Long> categories) {

        List<ProductCategory> result = new ArrayList<>();

        if (categories == null) {

            // Все товары без категорий в начало списка
            result.addAll(getAllProductsWithoutCategorySorted());

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

            // В товарах без категорий сортировать нечего
            return getAllProductsWithoutCategorySorted();

        } else {

            // Отбираем товары, которые принадлежат данным категориям

            // Получаем список товаров с категориями
            List<ProductCategory> products = productDao.getAllWithCategory();
            for (long category : categories) {
                for (ProductCategory product : products) {
                    if (product.getCategory().getId() == category) {
                        result.add(product);
                    }
                }
            }

            // Теперь сортируем по именам категорий
            result.sort(
                    Comparator.comparing(
                            (ProductCategory left) -> left.getCategory().getName() + left.getProduct().getName()
                    )
            );
        }

        return result;
    }

    /**
     * Получает список всех товаров, отсортированных по их именам
     *
     * @param categories категории, которым должны принадлежать товары.
     *                   Пустой список - все товары без категорий
     *                   Без списка - все товары
     * @return выборка товаров отсортированных по их именам
     */
    private List<ProductCategory> getAllProductsSortedByName(List<Long> categories) {

        List<ProductCategory> result = new ArrayList<>();

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
            Set<Product> resultSet = productDao.getAllWithCategories(categories);

            // Теперь переносим всё это в конечный результат
            resultSet.forEach((p) -> result.add(new ProductCategory(p, null)));
        }

        // Сортируем по именам товаров
        result.sort(Comparator.comparing((ProductCategory left) -> left.getProduct().getName()));

        return result;
    }

    /**
     * Получает список всех товаров без категории отсортированных по имени
     *
     * @return список всех товаров
     */
    private List<ProductCategory> getAllProductsWithoutCategorySorted() {
        List<ProductCategory> result = new ArrayList<>();

        // Все товары без категорий
        List<Product> products = productDao.getAllWithoutCategory();
        products.forEach((p) -> result.add(new ProductCategory(p, null)));

        // Сортируем по именам товаров
        result.sort(Comparator.comparing((ProductCategory left) -> left.getProduct().getName()));

        return result;
    }

}

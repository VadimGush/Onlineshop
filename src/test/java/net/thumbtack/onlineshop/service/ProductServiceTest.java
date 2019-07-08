package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.*;
import net.thumbtack.onlineshop.dto.ProductDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class ProductServiceTest {

    private ProductService productService;

    @Mock
    private ProductDao mockProductDao;

    @Mock
    private SessionDao mockSessionDao;

    @Mock
    private CategoryDao mockCategoryDao;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        productService = new ProductService(mockProductDao, mockSessionDao, mockCategoryDao);
    }

    @Test
    public void testAdd() throws ServiceException {
        setAdmin();

        // Все три категории найдены в БД
        when(mockCategoryDao.get(1)).thenReturn(new Category());
        when(mockCategoryDao.get(2)).thenReturn(new Category());
        when(mockCategoryDao.get(3)).thenReturn(new Category());

        ProductDto request = new ProductDto("product", 10, 1000,
                Arrays.asList(1, 2, 3));

        Product result = productService.add("token", request);

        // Продукт записан в БД
        verify(mockProductDao).insert(any());
        // Было получены записи о трёх категориях
        verify(mockCategoryDao, times(3)).get(anyLong());
        // Было вставлено три категори продукта
        verify(mockProductDao, times(3)).insertCategory(any());

        assertEquals(request.getName(), result.getName());
        assertEquals(request.getPrice(), (int)result.getPrice());
        assertEquals(request.getCount(), (int)result.getCount());
    }

    @Test
    public void testAddWithZeroCount() throws ServiceException {
        setAdmin();

        ProductDto request = new ProductDto("product", 10,0);

        Product result = productService.add("token", request);

        // Продукт записан в БД
        verify(mockProductDao).insert(any());

        assertEquals(request.getName(), result.getName());
        assertEquals(request.getPrice(), (int)result.getPrice());
        assertEquals(request.getCount(), (int)result.getCount());
    }

    @Test
    public void testAddWithoutCategories() throws ServiceException {
        setAdmin();

        ProductDto request = new ProductDto("product", 10, 1000);

        Product result = productService.add("token", request);

        // Продукт записан в БД
        verify(mockProductDao).insert(any());

        assertEquals(request.getName(), result.getName());
        assertEquals(request.getPrice(), (int)result.getPrice());
        assertEquals(request.getCount(), (int)result.getCount());
    }

    @Test(expected = ServiceException.class)
    public void testAddCategoryNotFound() throws ServiceException {
        setAdmin();

        when(mockCategoryDao.get(1)).thenReturn(new Category());
        when(mockCategoryDao.get(2)).thenReturn(null);
        when(mockCategoryDao.get(3)).thenReturn(new Category());

        ProductDto request = new ProductDto("product", 10, 1000,
                Arrays.asList(1, 2, 3));

        try {
            productService.add("token", request);
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.CATEGORY_NOT_FOUND, e.getErrorCode());

            // Было получены записи о трёх категориях
            verify(mockCategoryDao, times(2)).get(anyLong());

            // Никаких вставок в БД не было
            verify(mockProductDao, never()).insertCategory(any());
            verify(mockProductDao, never()).insert(any());

            throw e;
        }
    }

    @Test
    public void testEdit() throws ServiceException {
        setAdmin();

        // Старый товар найден в БД
        when(mockProductDao.get(0)).thenReturn(new Product("name", 1, 10));

        // Все три категори найдены в БД
        when(mockCategoryDao.get(1)).thenReturn(new Category());
        when(mockCategoryDao.get(2)).thenReturn(new Category());
        when(mockCategoryDao.get(3)).thenReturn(new Category());

        // Старый список категорий у продукта
        when(mockProductDao.getCategories(0)).thenReturn(
                Arrays.asList(new ProductCategory(), new ProductCategory())
        );

        ProductDto request = new ProductDto("new name", 2, 20, Arrays.asList(1, 2, 3));

        Product result = productService.edit("token", request, 0);

        // Получил старый товар из БД
        verify(mockProductDao).get(0);
        // Получил список категорий для старого товара
        verify(mockProductDao).getCategories(0);
        // Удалил старые категории
        verify(mockProductDao, times(2)).deleteCategory(any());
        // Добавил три новые категории
        verify(mockProductDao, times(3)).insertCategory(any());
        // И обновил товар
        verify(mockProductDao).update(any());

        // И в конце проверяем что всё изменили правильно
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getCount(), (int)result.getCount());
        assertEquals(request.getPrice(), (int)result.getPrice());
    }

    @Test
    public void testEditNoCategories() throws ServiceException {
        setAdmin();

        // Старый товар найден в БД
        when(mockProductDao.get(0)).thenReturn(new Product("name", 1, 10));

        ProductDto request = new ProductDto("new name", 2, 20);

        Product result = productService.edit("token", request, 0);

        // Получил старый товар из БД
        verify(mockProductDao).get(0);
        // Никаких удалений старых категори
        verify(mockProductDao, never()).deleteCategory(any());
        // И обновил товар
        verify(mockProductDao).update(any());

        // И в конце проверяем что всё изменили правильно
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getCount(), (int)result.getCount());
        assertEquals(request.getPrice(), (int)result.getPrice());
    }

    @Test(expected = ServiceException.class)
    public void testEditCategoryNotFound() throws ServiceException {
        setAdmin();

        // Старый товар найден в БД
        when(mockProductDao.get(0)).thenReturn(new Product("name", 1, 10));

        // Все три категори найдены в БД
        when(mockCategoryDao.get(1)).thenReturn(new Category());
        when(mockCategoryDao.get(2)).thenReturn(null);
        when(mockCategoryDao.get(3)).thenReturn(new Category());

        // Старый список категорий у продукта
        when(mockProductDao.getCategories(0)).thenReturn(
                Arrays.asList(new ProductCategory(), new ProductCategory())
        );

        ProductDto request = new ProductDto("new name", 2, 20, Arrays.asList(1, 2, 3));

        try {
            productService.edit("token", request, 0);
        } catch (ServiceException e) {
            verify(mockProductDao, never()).update(any());
            verify(mockProductDao, never()).insertCategory(any());
            verify(mockProductDao, never()).deleteCategory(any());

            assertEquals(ServiceException.ErrorCode.CATEGORY_NOT_FOUND, e.getErrorCode());
            throw e;
        }

    }

    @Test(expected = ServiceException.class)
    public void testEditProductNotFound() throws ServiceException {
        setAdmin();

        // Старый товар найден в БД
        when(mockProductDao.get(0)).thenReturn(null);

        ProductDto request = new ProductDto("new name", 2, 20);

        try {
            productService.edit("token", request, 0);
        } catch (ServiceException e) {
            verify(mockProductDao, never()).update(any());
            verify(mockProductDao, never()).insertCategory(any());
            verify(mockProductDao, never()).deleteCategory(any());

            assertEquals(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void testDelete() throws ServiceException {
        setAdmin();

        Product product = new Product();
        when(mockProductDao.get(0)).thenReturn(product);

        productService.delete("token", 0);

        verify(mockProductDao).delete(product);
    }

    @Test(expected = ServiceException.class)
    public void testDeleteProductNotFound() throws ServiceException {
        setAdmin();

        when(mockProductDao.get(0)).thenReturn(null);

        try {
            productService.get("token", 0);
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
            verify(mockProductDao, never()).delete(any());
            throw e;
        }
    }

    @Test
    public void testGet() throws ServiceException {
        setAdmin();

        Product product = new Product("product", 1, 20);
        product.setId(0L);
        when(mockProductDao.get(0)).thenReturn(product);

        Product result = productService.get("token", 0);

        verify(mockProductDao).get(0);

        assertEquals(product, result);
    }

    @Test(expected = ServiceException.class)
    public void testGetProductNotFound() throws ServiceException {
        setAdmin();

        when(mockProductDao.get(0)).thenReturn(null);

        try {
            productService.get("token", 0);
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void testGetCategories() throws ServiceException {
        setAdmin();

        Category category1 = new Category("category1");
        Category category2 = new Category("category2");
        Category category3 = new Category("category3");

        Product product = new Product("product", 1, 10);
        product.setId(1L);

        List<ProductCategory> categories = Arrays.asList(
                new ProductCategory(product, category1),
                new ProductCategory(product, category2),
                new ProductCategory(product, category3)
        );

        when(mockProductDao.getCategories(0)).thenReturn(categories);

        List<ProductCategory> result = productService.getCategories("token", 0);

        // Проверяем что реально был вызван соответствующий метод
        verify(mockProductDao).getCategories(0);

        assertEquals(categories.size(), result.size());
    }

    @Test
    public void testGetCategoriesEmpty() throws ServiceException {
        setAdmin();

        when(mockProductDao.getCategories(0)).thenReturn(Collections.emptyList());

        List<ProductCategory> result = productService.getCategories("token", 0);

        // Проверяем что реально был вызван соответствующий метод
        verify(mockProductDao).getCategories(0);

        assertEquals(0, result.size());
    }

    @Test
    public void testGetAll() throws ServiceException {
        setAdmin();

        // Получение всех товаров с сортировкой по именам

        List<Product> products = Arrays.asList(
                new Product("beretta", 1, 1),
                new Product("warhouse", 1, 1),
                new Product("amish", 1, 1)
        );
        when(mockProductDao.getAll()).thenReturn(products);

        List<ProductCategory> results = productService.getAll("token", null, ProductService.SortOrder.PRODUCT);

        verify(mockProductDao).getAll();

        assertEquals("amish", results.get(0).getProduct().getName());
        assertEquals("beretta", results.get(1).getProduct().getName());
        assertEquals("warhouse", results.get(2).getProduct().getName());
        assertNull(results.get(0).getCategory());
        assertNull(results.get(1).getCategory());
        assertNull(results.get(2).getCategory());
    }

    @Test
    public void testGetAllWithEmptyCategories() throws ServiceException {
        setAdmin();

        // Получаем список всех товаров, не принадлежащих не одной категори
        // отсортированный по именами

        List<Product> products = Arrays.asList(
                new Product("beretta", 1, 1),
                new Product("warhouse", 1, 1),
                new Product("amish", 1, 1)
        );
        when(mockProductDao.getAllWithoutCategory()).thenReturn(products);

        List<ProductCategory> results =
                productService.getAll("token", Collections.emptyList(), ProductService.SortOrder.PRODUCT);

        verify(mockProductDao).getAllWithoutCategory();
        verify(mockProductDao, never()).getAll();
        verify(mockProductDao, never()).getAllWithCategory();

        assertEquals("amish", results.get(0).getProduct().getName());
        assertEquals("beretta", results.get(1).getProduct().getName());
        assertEquals("warhouse", results.get(2).getProduct().getName());
        assertNull(results.get(0).getCategory());
        assertNull(results.get(1).getCategory());
        assertNull(results.get(2).getCategory());
    }

    @Test
    public void testGetAllWithCategories() throws ServiceException {
        setAdmin();

        // Получаем список товаров отсортированный по именам
        // которые принадлежат хотя бы одной из указанных категорий

        Category first = new Category("aa");
        first.setId(1L);
        Category second = new Category("bb");
        second.setId(2L);

        List<ProductCategory> productCategory = Arrays.asList(
                new ProductCategory(new Product("warka", 1, 1), first),
                new ProductCategory(new Product("amka", 1, 1), second),
                new ProductCategory(new Product("arka", 1, 1), first)
        );

        when(mockProductDao.getAllWithCategory()).thenReturn(productCategory);

        List<ProductCategory> results =
                productService.getAll("token", Arrays.asList(1), ProductService.SortOrder.PRODUCT);

        verify(mockProductDao).getAllWithCategory();

        assertEquals("arka", results.get(0).getProduct().getName());
        assertEquals("warka", results.get(1).getProduct().getName());

        assertNull(results.get(0).getCategory());
        assertNull(results.get(1).getCategory());
    }

    @Test
    public void testGetAllCategorySortedWithCategories() throws ServiceException {
        setAdmin();

        // Получаем список товаров, отсортированных по именам категорий, которыe
        // принадлежат хотя бы одной из указанных категорий (ТЗ ПРОСТО ОГОНЬ!)

        Category first = new Category("xx");
        first.setId(1L);
        Category second = new Category("bb");
        second.setId(2L);
        Category third = new Category("aa");
        third.setId(3L);

        List<ProductCategory> productCategory = Arrays.asList(
                new ProductCategory(new Product("warka", 1, 1), first),
                new ProductCategory(new Product("amka", 1, 1), second),
                new ProductCategory(new Product("arka", 1, 1), first),
                new ProductCategory(new Product("xen", 1, 1), third)
        );
        when(mockProductDao.getAllWithCategory()).thenReturn(productCategory);

        List<ProductCategory> results =
                productService.getAll("token", Arrays.asList(1, 3), ProductService.SortOrder.CATEGORY);

        verify(mockProductDao).getAllWithCategory();

        assertEquals("xen", results.get(0).getProduct().getName());
        assertEquals("arka", results.get(1).getProduct().getName());
        assertEquals("amka", results.get(2).getProduct().getName());

        assertEquals("aa", results.get(0).getCategory());
        assertEquals("xx", results.get(1).getCategory());
        assertEquals("xx", results.get(2).getCategory());
    }

    @Test
    public void testGetAllCategorySortedWithEmptyCategories() throws ServiceException {
        setAdmin();

        // Получаем список всех товаров, не принадлежащих не одной категории
        // (без сортировки)

        List<Product> products = Arrays.asList(
                new Product("beretta", 1, 1),
                new Product("warhouse", 1, 1),
                new Product("amish", 1, 1)
        );
        when(mockProductDao.getAllWithoutCategory()).thenReturn(products);

        List<ProductCategory> results =
                productService.getAll("token", Collections.emptyList(), ProductService.SortOrder.CATEGORY);

        verify(mockProductDao).getAllWithoutCategory();
        verify(mockProductDao, never()).getAll();
        verify(mockProductDao, never()).getAllWithCategory();

        assertEquals("beretta", results.get(0).getProduct().getName());
        assertEquals("warhouse", results.get(1).getProduct().getName());
        assertEquals("amish", results.get(2).getProduct().getName());
        assertNull(results.get(0).getCategory());
        assertNull(results.get(1).getCategory());
        assertNull(results.get(2).getCategory());
    }

    @Test
    public void testGetAllCategorySorted() throws ServiceException {
        setAdmin();

        // Получаем список всех товаров, отстортированных по именам категорий

        List<Product> products = Arrays.asList(
                new Product("beretta", 1,1),
                new Product("warhouse", 1, 1),
                new Product("amish", 1, 1)
        );

        List<ProductCategory> productCategory = Arrays.asList(
                new ProductCategory(new Product("b", 1,1), new Category("zet")),
                new ProductCategory(new Product("a", 1, 1), new Category("zet")),
                new ProductCategory(new Product("ob", 1, 1), new Category("ark"))
        );

        when(mockProductDao.getAllWithoutCategory()).thenReturn(products);
        when(mockProductDao.getAllWithCategory()).thenReturn(productCategory);

        List<ProductCategory> results =
                productService.getAll("token", null, ProductService.SortOrder.CATEGORY);

        verify(mockProductDao).getAllWithoutCategory();
        verify(mockProductDao).getAllWithCategory();

        assertEquals("amish", results.get(0).getProduct().getName());
        assertEquals("beretta", results.get(1).getProduct().getName());
        assertEquals("warhouse", results.get(2).getProduct().getName());
        assertNull(results.get(0).getCategory());
        assertNull(results.get(1).getCategory());
        assertNull(results.get(2).getCategory());

        assertEquals("ob", results.get(3).getProduct().getName());
        assertEquals("a", results.get(4).getProduct().getName());
        assertEquals("b", results.get(5).getProduct().getName());

        assertEquals("ark", results.get(3).getCategory().getName());
        assertEquals("zet", results.get(4).getCategory().getName());
        assertEquals("zet", results.get(5).getCategory().getName());
    }

    public void setAdmin() {
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));
    }

    @Test
    public void testNotAdmin() {
        Account client = generateClient();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        try {
            productService.add("token", null);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_ADMIN, e.getErrorCode());
        }

        try {
            productService.edit("token", null, 0);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_ADMIN, e.getErrorCode());
        }

        try {
            productService.delete("token", 0);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_ADMIN, e.getErrorCode());
        }

    }

    @Test
    public void testNotLogin() {
        when(mockSessionDao.get("token")).thenReturn(null);

        try {
            productService.add("token", null);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }

        try {
            productService.edit("token", null, 0);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }

        try {
            productService.delete("token", 0);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }

        try {
            productService.get("token", 0);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }

        try {
            productService.getCategories("token", 0);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }

        try {
            productService.getAll("token", null, null);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }

    }

    private Account generateAdmin() {
        return AccountFactory.createAdmin(
                "vadim", "gush", "vadimovich", "coder", "vadim", "Iddqd225"
        );
    }

    private Account generateClient() {
        return AccountFactory.createClient(
                "234324", "234324", "wereww1", "werewr1",
                "wrewf3", "werwer235", "werw23", "ewr23423"
        );
    }
}

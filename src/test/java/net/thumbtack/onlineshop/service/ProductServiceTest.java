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
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

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
        verify(mockCategoryDao, times(3)).get(any());
        // Было вставлено три категори продукта
        verify(mockProductDao, times(3)).insertCategory(any());

        assertEquals(request.getName(), result.getName());
        assertEquals(request.getPrice(), (int)result.getPrice());
        assertEquals(request.getCount(), (int)result.getCount());
    }

    @Test
    public void testAddWithZeroCount() throws ServiceException {
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

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
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

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
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

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
            verify(mockCategoryDao, times(2)).get(any());

            // Никаких вставок в БД не было
            verify(mockProductDao, never()).insertCategory(any());
            verify(mockProductDao, never()).insert(any());

            throw e;
        }
    }

    @Test
    public void testEdit() throws ServiceException {
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

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
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

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
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

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

    @Test
    public void testEditProductNotFound() throws ServiceException {
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

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
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

        Product product = new Product();
        when(mockProductDao.get(0)).thenReturn(product);

        productService.delete("token", 0);

        verify(mockProductDao).delete(product);
    }

    @Test(expected = ServiceException.class)
    public void testDeleteProductNotFound() throws ServiceException {
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

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
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

        Product product = new Product("product", 1, 20);
        product.setId(0L);
        when(mockProductDao.get(0)).thenReturn(product);

        Product result = productService.get("token", 0);

        verify(mockProductDao).get(0);

        assertEquals(product, result);
    }

    @Test(expected = ServiceException.class)
    public void testGetProductNotFound() throws ServiceException {
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

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
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

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
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

        when(mockProductDao.getCategories(0)).thenReturn(Collections.emptyList());

        List<ProductCategory> result = productService.getCategories("token", 0);

        // Проверяем что реально был вызван соответствующий метод
        verify(mockProductDao).getCategories(0);

        assertEquals(0, result.size());
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

package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.*;
import net.thumbtack.onlineshop.dto.ProductDto;
import net.thumbtack.onlineshop.dto.ProductEditDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

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

        Answer<Product> answer = (invocation) -> {
            Product product = invocation.getArgument(0);
            product.setId(0L);

            return null;
        };
        // База данных должна обновлять id объектов, которые в неё вставляют
        // но поскольку стандартный мок сам этого делать не умеет, то приходится писать реализацию
        doAnswer(answer).when(mockProductDao).insert(any());
        doAnswer(answer).when(mockProductDao).update(any());
    }

    @Test
    public void testAdd() throws ServiceException {
        setAdmin();

        // Все три категории найдены в БД
        when(mockCategoryDao.get(1)).thenReturn(new Category());
        when(mockCategoryDao.get(2)).thenReturn(new Category());
        when(mockCategoryDao.get(3)).thenReturn(new Category());

        ProductDto request = new ProductDto("product", 10, 1000,
                Arrays.asList(1L, 2L, 3L));

        ProductDto result = productService.add("token", request);

        // Продукт записан в БД
        verify(mockProductDao).insert(any());
        // Было получены записи о трёх категориях
        verify(mockCategoryDao, times(3)).get(anyLong());
        // Было вставлено три категори продукта
        verify(mockProductDao, times(3)).insertCategory(any());

        assertEquals(request.getName(), result.getName());
        assertEquals((int)request.getPrice(), (int)result.getPrice());
        assertEquals((int)request.getCount(), (int)result.getCount());
    }

    @Test
    public void testAddWithZeroCount() throws ServiceException {
        setAdmin();

        ProductDto request = new ProductDto("product", 10,0);

        ProductDto result = productService.add("token", request);

        // Продукт записан в БД
        verify(mockProductDao).insert(any());

        assertEquals(request.getName(), result.getName());
        assertEquals((int)request.getPrice(), (int)result.getPrice());
        assertEquals((int)request.getCount(), (int)result.getCount());
    }

    @Test
    public void testAddWithoutCategories() throws ServiceException {
        setAdmin();

        ProductDto request = new ProductDto("product", 10, 1000);

        ProductDto result = productService.add("token", request);

        // Продукт записан в БД
        verify(mockProductDao).insert(any());

        assertEquals(request.getName(), result.getName());
        assertEquals((int)request.getPrice(), (int)result.getPrice());
        assertEquals((int)request.getCount(), (int)result.getCount());
    }

    @Test(expected = ServiceException.class)
    public void testAddCategoryNotFound() throws ServiceException {
        setAdmin();

        when(mockCategoryDao.get(1)).thenReturn(new Category());
        when(mockCategoryDao.get(2)).thenReturn(null);
        when(mockCategoryDao.get(3)).thenReturn(new Category());

        ProductDto request = new ProductDto("product", 10, 1000,
                Arrays.asList(1L, 2L, 3L));

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
        for (long i = 1; i <= 3; ++i) {
            Category category = new Category();
            category.setId(i);
            when(mockCategoryDao.get(i)).thenReturn(category);
        }

        // Старый список категорий у продукта
        when(mockProductDao.getCategories(0)).thenReturn(
                Arrays.asList(new ProductCategory(), new ProductCategory())
        );

        ProductEditDto request = new ProductEditDto("new name", 2, 20, Arrays.asList(1L, 2L, 3L));

        ProductDto result = productService.edit("token", request, 0);

        // Получил старый товар из БД
        verify(mockProductDao).get(0);
        // Получил список категорий для старого товара
        verify(mockProductDao, times(2)).getCategories(0);
        // Удалил старые категории
        verify(mockProductDao, times(2)).deleteCategory(any());
        // Добавил три новые категории
        verify(mockProductDao, times(3)).insertCategory(any());
        // И обновил товар
        verify(mockProductDao).update(any());

        // И в конце проверяем что всё изменили правильно
        assertEquals(request.getName(), result.getName());
        assertEquals((int)request.getCount(), (int)result.getCount());
        assertEquals((int)request.getPrice(), (int)result.getPrice());
    }

    @Test
    public void testEditNoCategories() throws ServiceException {
        setAdmin();

        // Старый товар найден в БД
        when(mockProductDao.get(0)).thenReturn(new Product("name", 1, 10));

        ProductEditDto request = new ProductEditDto("new name", 2, 20);

        ProductDto result = productService.edit("token", request, 0);

        // Получил старый товар из БД
        verify(mockProductDao).get(0);
        // Никаких удалений старых категори
        verify(mockProductDao, never()).deleteCategory(any());
        // И обновил товар
        verify(mockProductDao).update(any());

        // И в конце проверяем что всё изменили правильно
        assertEquals(request.getName(), result.getName());
        assertEquals((int)request.getCount(), (int)result.getCount());
        assertEquals((int)request.getPrice(), (int)result.getPrice());
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

        ProductEditDto request = new ProductEditDto("new name", 2, 20, Arrays.asList(1L, 2L, 3L));

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

        ProductEditDto request = new ProductEditDto("new name", 2, 20);

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

        ProductCategory category1 = new ProductCategory(product, new Category("category1"));
        ProductCategory category2 = new ProductCategory(product, new Category("category2"));
        when(mockProductDao.get(0)).thenReturn(product);
        when(mockProductDao.getCategories(0)).thenReturn(
                Arrays.asList(category1, category2)
        );

        productService.delete("token", 0);

        verify(mockProductDao).delete(product);
        verify(mockProductDao).deleteCategory(category1);
        verify(mockProductDao).deleteCategory(category2);
    }

    @Test(expected = ServiceException.class)
    public void testDeleteProductNotFound() throws ServiceException {
        setAdmin();

        when(mockProductDao.get(0)).thenReturn(null);

        try {
            productService.delete("token", 0);
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

        ProductDto result = productService.get("token", 0);

        verify(mockProductDao).get(0);

        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getCount(), result.getCount());
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
    public void testGetAll() throws ServiceException {
        setAdmin();

        // Получение всех товаров с сортировкой по именам

        Product product1 = new Product("beretta", 1, 1);
        product1.setId(1L);
        Product product2 = new Product("warhouse", 1, 1);
        product2.setId(2L);
        Product product3 = new Product("amish", 1, 1);
        product3.setId(3L);

        List<Product> products = Arrays.asList(
                product1,
                product2,
                product3
        );
        when(mockProductDao.getAll()).thenReturn(products);

        List<ProductDto> results = productService.getAll("token", null, ProductService.SortOrder.PRODUCT);

        verify(mockProductDao).getAll();

        assertEquals("amish", results.get(0).getName());
        assertEquals("beretta", results.get(1).getName());
        assertEquals("warhouse", results.get(2).getName());
        assertNull(results.get(0).getCategories());
        assertNull(results.get(1).getCategories());
        assertNull(results.get(2).getCategories());
    }

    @Test
    public void testGetAllWithEmptyCategories() throws ServiceException {
        setAdmin();

        // Получаем список всех товаров, не принадлежащих не одной категори
        // отсортированный по именами

        Product product1 = new Product("beretta", 1, 1);
        product1.setId(1L);
        Product product2 = new Product("warhouse", 1, 1);
        product2.setId(2L);
        Product product3 = new Product("amish", 1, 1);
        product3.setId(3L);

        List<Product> products = Arrays.asList(
                product1,
                product2,
                product3
        );
        when(mockProductDao.getAllWithoutCategory()).thenReturn(products);

        List<ProductDto> results =
                productService.getAll("token", Collections.emptyList(), ProductService.SortOrder.PRODUCT);

        verify(mockProductDao).getAllWithoutCategory();
        verify(mockProductDao, never()).getAll();
        verify(mockProductDao, never()).getAllWithCategory();

        assertEquals("amish", results.get(0).getName());
        assertEquals("beretta", results.get(1).getName());
        assertEquals("warhouse", results.get(2).getName());
        assertNull(results.get(0).getCategories());
        assertNull(results.get(1).getCategories());
        assertNull(results.get(2).getCategories());
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

        Product product1 = new Product("warka", 1, 1);
        product1.setId(1L);
        Product product2 = new Product("amka", 1, 1);
        product2.setId(2L);
        Product product3 = new Product("arka", 1, 1);
        product3.setId(3L);

        List<ProductCategory> productCategory = Arrays.asList(
                new ProductCategory(product1, first),
                new ProductCategory(product2, second),
                new ProductCategory(product3, first)
        );

        for (ProductCategory category : productCategory) {
            when(mockProductDao.getCategories(category.getProduct().getId()))
                    .thenReturn(Collections.singletonList(category));
        }

        when(mockProductDao.getAllWithCategory()).thenReturn(productCategory);

        List<ProductDto> results =
                productService.getAll("token", Collections.singletonList(1L), ProductService.SortOrder.PRODUCT);

        verify(mockProductDao).getAllWithCategory();

        assertEquals("arka", results.get(0).getName());
        assertEquals("warka", results.get(1).getName());

        assertEquals(1, (long)results.get(0).getCategories().get(0));
        assertEquals(1, (long)results.get(1).getCategories().get(0));
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

        Product product1 = new Product("warka", 1, 1);
        product1.setId(1L);
        Product product2 = new Product("amka", 1, 1);
        product2.setId(2L);
        Product product3 = new Product("arka", 1, 1);
        product3.setId(3L);
        Product product4 = new Product("xen", 1, 1);
        product4.setId(4L);

        List<ProductCategory> productCategory = Arrays.asList(
                new ProductCategory(product1, first),
                new ProductCategory(product2, second),
                new ProductCategory(product3, first),
                new ProductCategory(product4, third)
        );

        for (ProductCategory category : productCategory) {
            when(mockProductDao.getCategories(category.getProduct().getId()))
                    .thenReturn(Collections.singletonList(category));
        }

        when(mockProductDao.getAllWithCategory()).thenReturn(productCategory);

        List<ProductDto> results =
                productService.getAll("token", Arrays.asList(1L, 3L), ProductService.SortOrder.CATEGORY);

        verify(mockProductDao).getAllWithCategory();

        assertEquals("xen", results.get(0).getName());
        assertEquals("arka", results.get(1).getName());
        assertEquals("warka", results.get(2).getName());

        assertEquals(3, (long)results.get(0).getCategories().get(0));
        assertEquals(1, (long)results.get(1).getCategories().get(0));
        assertEquals(1, (long)results.get(2).getCategories().get(0));
    }

    @Test
    public void testGetAllCategorySortedWithEmptyCategories() throws ServiceException {
        setAdmin();

        // Получаем список всех товаров, не принадлежащих не одной категории
        // (без сортировки)

        Product product1 = new Product("beretta", 1, 1);
        product1.setId(1L);
        Product product2 = new Product("warhouse", 1, 1);
        product2.setId(2L);
        Product product3 = new Product("amish", 1, 1);
        product3.setId(3L);

        List<Product> products = Arrays.asList(
                product1,
                product2,
                product3
        );

        when(mockProductDao.getAllWithoutCategory()).thenReturn(products);

        List<ProductDto> results =
                productService.getAll("token", Collections.emptyList(), ProductService.SortOrder.CATEGORY);

        verify(mockProductDao).getAllWithoutCategory();
        verify(mockProductDao, never()).getAll();
        verify(mockProductDao, never()).getAllWithCategory();

        assertEquals("amish", results.get(0).getName());
        assertEquals("beretta", results.get(1).getName());
        assertEquals("warhouse", results.get(2).getName());
        assertNull(results.get(0).getCategories());
        assertNull(results.get(1).getCategories());
        assertNull(results.get(2).getCategories());
    }

    @Test
    public void testGetAllCategorySorted() throws ServiceException {
        setAdmin();

        // Получаем список всех товаров, отстортированных по именам категорий

        // ИНДУСИМ ПО ПОЛНОЙ
        Product product1 = new Product("beretta", 1, 1);

        product1.setId(1L);
        Product product2 = new Product("warhouse", 1, 1);
        product2.setId(2L);
        Product product3 = new Product("amish", 1, 1);
        product3.setId(3L);

        Product product4 = new Product("b", 1, 1);
        product4.setId(4L);
        Product product5 = new Product("a", 1, 1);
        product5.setId(5L);
        Product product6 = new Product("ob", 1, 1);
        product6.setId(6L);

        Category category1 = new Category("zet");
        category1.setId(1L);
        Category category2 = new Category("ark");
        category2.setId(2L);

        List<Product> products = Arrays.asList(
                product1,
                product2,
                product3
        );

        List<ProductCategory> productCategory = Arrays.asList(
                new ProductCategory(product4, category1),
                new ProductCategory(product5, category1),
                new ProductCategory(product6, category2)
        );

        for (ProductCategory category : productCategory) {
            when(mockProductDao.getCategories(category.getProduct().getId()))
                    .thenReturn(Collections.singletonList(category));
        }

        when(mockProductDao.getAllWithoutCategory()).thenReturn(products);
        when(mockProductDao.getAllWithCategory()).thenReturn(productCategory);

        List<ProductDto> results =
                productService.getAll("token", null, ProductService.SortOrder.CATEGORY);

        verify(mockProductDao).getAllWithoutCategory();
        verify(mockProductDao).getAllWithCategory();

        assertEquals("amish", results.get(0).getName());
        assertEquals("beretta", results.get(1).getName());
        assertEquals("warhouse", results.get(2).getName());
        assertNull(results.get(0).getCategories());
        assertNull(results.get(1).getCategories());
        assertNull(results.get(2).getCategories());

        assertEquals("ob", results.get(3).getName());
        assertEquals("a", results.get(4).getName());
        assertEquals("b", results.get(5).getName());

        assertEquals(2, (long)results.get(3).getCategories().get(0));
        assertEquals(1, (long)results.get(4).getCategories().get(0));
        assertEquals(1, (long)results.get(5).getCategories().get(0));
    }


    private void setAdmin() {
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));
    }

    @Test
    public void testNotAdmin() {
        Account client = generateClient();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        try {
            productService.add("token", new ProductDto());
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
            productService.add("token", new ProductDto());
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

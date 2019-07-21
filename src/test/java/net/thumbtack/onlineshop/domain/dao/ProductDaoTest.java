package net.thumbtack.onlineshop.domain.dao;

import net.thumbtack.onlineshop.domain.models.Category;
import net.thumbtack.onlineshop.domain.models.Product;
import net.thumbtack.onlineshop.domain.models.ProductCategory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProductDaoTest {

    private ProductDao productDao;

    @Mock
    private EntityManager mockEntityManager;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        productDao = new ProductDao(mockEntityManager);
    }

    @Test
    public void testInsert() {
        Product product = new Product();

        productDao.insert(product);

        verify(mockEntityManager).persist(product);
    }

    @Test
    public void testUpdate() {
        Product product = new Product();

        productDao.update(product);

        verify(mockEntityManager).merge(product);
    }

    @Test
    public void testDelete() {
        Product product = new Product();
        when(mockEntityManager.merge(product)).thenReturn(product);

        productDao.delete(product);

        assertTrue(product.getDeleted());
        verify(mockEntityManager).merge(product);
    }

    @Test
    public void testInsertCategory() {
        ProductCategory category = new ProductCategory();

        productDao.insertCategory(category);

        verify(mockEntityManager).persist(category);
    }

    @Test
    public void testDeleteCategory() {
        ProductCategory category = new ProductCategory();
        when(mockEntityManager.merge(category)).thenReturn(category);

        productDao.deleteCategory(category);

        verify(mockEntityManager).merge(category);
        verify(mockEntityManager).remove(category);
    }

    @Test
    public void testGetCategories() {

        List<ProductCategory> list = Arrays.asList(
                new ProductCategory(),
                new ProductCategory()
        );

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<ProductCategory> mockCriteriaQuery = (CriteriaQuery<ProductCategory>) mock(CriteriaQuery.class);
        TypedQuery<ProductCategory> mockTypedQuery = (TypedQuery<ProductCategory>) mock(TypedQuery.class);
        Root<ProductCategory> mockRoot = (Root<ProductCategory>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(ProductCategory.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(ProductCategory.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(list);

        List<ProductCategory> result = productDao.getCategories(0L);

        verify(mockCriteriaQuery).from(ProductCategory.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        verify(mockRoot).get("product");
        verify(mockCriteriaBuilder).equal(null, 0L);

        assertEquals(list.size(), result.size());
    }

    @Test
    public void testGetAll() {

        List<Product> list = Arrays.asList(
                new Product(),
                new Product()
        );

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Product> mockCriteriaQuery = (CriteriaQuery<Product>) mock(CriteriaQuery.class);
        TypedQuery<Product> mockTypedQuery = (TypedQuery<Product>) mock(TypedQuery.class);
        Root<Product> mockRoot = (Root<Product>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Product.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Product.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(list);

        List<Product> result = productDao.getAll();

        verify(mockCriteriaQuery).from(Product.class);
        verify(mockCriteriaQuery).select(mockRoot);

        assertEquals(list.size(), result.size());

    }

    @Test
    public void testGet() {
        Product product = new Product();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Product> mockCriteriaQuery = (CriteriaQuery<Product>) mock(CriteriaQuery.class);
        TypedQuery<Product> mockTypedQuery = (TypedQuery<Product>) mock(TypedQuery.class);
        Root<Product> mockRoot = (Root<Product>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Product.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Product.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(product);

        Product result = productDao.get(0L);

        verify(mockCriteriaQuery).from(Product.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        verify(mockRoot).get("id");
        verify(mockCriteriaBuilder).equal(null, 0L);

        assertEquals(product, result);
    }

    @Test
    public void testGetNull() {

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Product> mockCriteriaQuery = (CriteriaQuery<Product>) mock(CriteriaQuery.class);
        TypedQuery<Product> mockTypedQuery = (TypedQuery<Product>) mock(TypedQuery.class);
        Root<Product> mockRoot = (Root<Product>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Product.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Product.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        assertNull(productDao.get(0L));

        verify(mockCriteriaQuery).from(Product.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        verify(mockRoot).get("id");
        verify(mockCriteriaBuilder).equal(null, 0L);

    }

    @Test
    public void testAllWithCategory() {

        List<ProductCategory> categories = new ArrayList<>();
        Product product1 = new Product();
        product1.setDeleted(false);
        Product product2 = new Product();
        product2.setDeleted(true);
        categories.add(new ProductCategory(product1, new Category("yes")));
        categories.add(new ProductCategory(product2, new Category("no")));

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<ProductCategory> mockCriteriaQuery = (CriteriaQuery<ProductCategory>) mock(CriteriaQuery.class);
        TypedQuery<ProductCategory> mockTypedQuery = (TypedQuery<ProductCategory>) mock(TypedQuery.class);
        Root<ProductCategory> mockRoot = (Root<ProductCategory>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(ProductCategory.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(ProductCategory.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(categories);

        List<ProductCategory> result = productDao.getAllWithCategory();

        assertEquals(1, result.size());
        assertEquals("yes", result.get(0).getCategory().getName());

        // Был селект
        verify(mockCriteriaQuery).from(ProductCategory.class);
        verify(mockCriteriaQuery).select(mockRoot);

    }

    @Test
    public void testAllWithoutCategory() {

        List<Product> products = Arrays.asList(
                new Product(), new Product()
        );
        Query mockQuery = mock(Query.class);
        when(mockEntityManager.createNativeQuery(any(), eq(Product.class))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(products);

        List<Product> result = productDao.getAllWithoutCategory();

        verify(mockEntityManager).createNativeQuery(
                "select a.* from product a left join productcategory b on" +
                        " a.id = b.product_id where b.category_id is NULL and deleted = 0"
        , Product.class);

        verify(mockQuery).getResultList();

        assertEquals(products, result);
    }

    @Test
    public void testClear() {

        Query mockQuery = mock(Query.class);
        when(mockEntityManager.createNativeQuery(any()))
                .thenReturn(mockQuery);

        productDao.clear();

        verify(mockEntityManager).createNativeQuery("delete from productcategory");
        verify(mockEntityManager).createNativeQuery("delete from product");
        verify(mockQuery, times(2)).executeUpdate();
    }

}


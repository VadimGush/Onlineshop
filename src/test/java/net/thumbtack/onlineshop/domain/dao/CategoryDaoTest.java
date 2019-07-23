package net.thumbtack.onlineshop.domain.dao;

import net.thumbtack.onlineshop.domain.models.Category;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CategoryDaoTest {

    private CategoryDao categoryDao;

    @Mock
    private EntityManager mockEntityManager;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        categoryDao = new CategoryDao(mockEntityManager);
    }

    @Test
    public void testInsert() {
        Category category = new Category();

        categoryDao.insert(category);

        verify(mockEntityManager).persist(category);
    }

    @Test
    public void testUpdate() {
        Category category = new Category();

        categoryDao.update(category);

        verify(mockEntityManager).merge(category);
    }

    @Test
    public void testDelete() {
        Category category = new Category();
        when(mockEntityManager.merge(category)).thenReturn(category);

        categoryDao.delete(category);

        verify(mockEntityManager).merge(category);
        verify(mockEntityManager).remove(category);
    }

    @Test
    public void testExists() {

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> mockCriteriaQuery = (CriteriaQuery<Long>) mock(CriteriaQuery.class);
        TypedQuery<Long> mockTypedQuery = (TypedQuery<Long>) mock(TypedQuery.class);
        Root<Category> mockRoot = (Root<Category>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Long.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Category.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(1L);

        assertTrue(categoryDao.exists("value"));

        verify(mockCriteriaQuery).from(Category.class);
        verify(mockCriteriaQuery).select(any());
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        verify(mockRoot).get("name");

        verify(mockCriteriaBuilder).equal(null, "value");
    }

    @Test
    public void testNotExists() {

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> mockCriteriaQuery = (CriteriaQuery<Long>) mock(CriteriaQuery.class);
        TypedQuery<Long> mockTypedQuery = (TypedQuery<Long>) mock(TypedQuery.class);
        Root<Category> mockRoot = (Root<Category>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Long.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Category.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(0L);

        assertFalse(categoryDao.exists("value"));

        verify(mockCriteriaQuery).from(Category.class);
        verify(mockCriteriaQuery).select(any());
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        verify(mockRoot).get("name");

        verify(mockCriteriaBuilder).equal(null, "value");
    }

    @Test
    public void testGet() {

        Category category = new Category();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Category> mockCriteriaQuery = (CriteriaQuery<Category>) mock(CriteriaQuery.class);
        TypedQuery<Category> mockTypedQuery = (TypedQuery<Category>) mock(TypedQuery.class);
        Root<Category> mockRoot = (Root<Category>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Category.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Category.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(category);

        Category result = categoryDao.get(0L);

        verify(mockCriteriaQuery).from(Category.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        verify(mockRoot).get("id");

        verify(mockCriteriaBuilder).equal(null, 0L);

        assertEquals(category, result);
    }

    @Test
    public void testGetNull() {

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Category> mockCriteriaQuery = (CriteriaQuery<Category>) mock(CriteriaQuery.class);
        TypedQuery<Category> mockTypedQuery = (TypedQuery<Category>) mock(TypedQuery.class);
        Root<Category> mockRoot = (Root<Category>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Category.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Category.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        assertNull(categoryDao.get(0L));

        verify(mockCriteriaQuery).from(Category.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        verify(mockRoot).get("id");

        verify(mockCriteriaBuilder).equal(null, 0L);
    }

    @Test
    public void testGetAll() {

        List<Category> list = Arrays.asList(
                new Category(),
                new Category()
        );

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Category> mockCriteriaQuery = (CriteriaQuery<Category>) mock(CriteriaQuery.class);
        TypedQuery<Category> mockTypedQuery = (TypedQuery<Category>) mock(TypedQuery.class);
        Root<Category> mockRoot = (Root<Category>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Category.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Category.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(list);

        List<Category> result = categoryDao.getAll();

        verify(mockCriteriaQuery).from(Category.class);
        verify(mockCriteriaQuery).select(mockRoot);

        assertEquals(list.size(), result.size());
    }

    @Test
    public void testClear() {
        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaDelete<Category> mockCriteria = (CriteriaDelete<Category>) mock(CriteriaDelete.class);
        Query mockQuery = mock(Query.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createCriteriaDelete(Category.class)).thenReturn(mockCriteria);
        when(mockEntityManager.createQuery(mockCriteria)).thenReturn(mockQuery);

        categoryDao.clear();

        verify(mockCriteria).from(Category.class);
        verify(mockEntityManager).createQuery(mockCriteria);
        verify(mockQuery).executeUpdate();
    }

}

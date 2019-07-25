package net.thumbtack.onlineshop.domain.dao;

import net.thumbtack.onlineshop.domain.models.Purchase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class PurchaseDaoTest {

    private PurchaseDao purchaseDao;

    @Mock
    private EntityManager mockEntityManager;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        purchaseDao = new PurchaseDao(mockEntityManager);
    }

    @Test
    public void testInsert() {
        Purchase purchase = new Purchase();

        purchaseDao.insert(purchase);

        verify(mockEntityManager).persist(purchase);
    }

    @Test
    public void testGetPurchasesSortedByProducts() {
        List<Purchase> expected = new ArrayList<>();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Purchase> mockCriteriaQuery = (CriteriaQuery<Purchase>) mock(CriteriaQuery.class);
        TypedQuery<Purchase> mockTypedQuery = (TypedQuery<Purchase>) mock(TypedQuery.class);
        Root<Purchase> mockRoot = (Root<Purchase>) mock(Root.class);
        Order mockOrder = mock(Order.class);
        Path mockPath = mock(Path.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Purchase.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Purchase.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);

        when(mockTypedQuery.setFirstResult(1)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setMaxResults(2)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expected);

        when(mockRoot.get("product")).thenReturn(mockPath);
        when(mockCriteriaBuilder.asc(mockPath)).thenReturn(mockOrder);

        List<Purchase> result = purchaseDao.getPurchasesSortedByProducts(2, 1);

        verify(mockCriteriaQuery).from(Purchase.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).orderBy(mockOrder);

        verify(mockEntityManager).createQuery(mockCriteriaQuery);
        verify(mockTypedQuery).setFirstResult(1);
        verify(mockTypedQuery).setMaxResults(2);

        assertEquals(expected, result);
    }

    @Test
    public void testGetPurchasesSortedByClients() {
        List<Purchase> expected = new ArrayList<>();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Purchase> mockCriteriaQuery = (CriteriaQuery<Purchase>) mock(CriteriaQuery.class);
        TypedQuery<Purchase> mockTypedQuery = (TypedQuery<Purchase>) mock(TypedQuery.class);
        Root<Purchase> mockRoot = (Root<Purchase>) mock(Root.class);
        Order mockOrder = mock(Order.class);
        Path mockPath = mock(Path.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Purchase.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Purchase.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);

        when(mockTypedQuery.setFirstResult(1)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setMaxResults(2)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expected);

        when(mockRoot.get("account")).thenReturn(mockPath);
        when(mockCriteriaBuilder.asc(mockPath)).thenReturn(mockOrder);

        List<Purchase> result = purchaseDao.getPurchasesSortedByClients(2, 1);

        verify(mockCriteriaQuery).from(Purchase.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).orderBy(mockOrder);

        verify(mockEntityManager).createQuery(mockCriteriaQuery);
        verify(mockTypedQuery).setFirstResult(1);
        verify(mockTypedQuery).setMaxResults(2);

        assertEquals(expected, result);
    }

    @Test
    public void testGetClientPurchases() {
        List<Purchase> expected = new ArrayList<>();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Purchase> mockCriteriaQuery = (CriteriaQuery<Purchase>) mock(CriteriaQuery.class);
        TypedQuery<Purchase> mockTypedQuery = (TypedQuery<Purchase>) mock(TypedQuery.class);
        Root<Purchase> mockRoot = (Root<Purchase>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Purchase.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Purchase.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);

        when(mockTypedQuery.setFirstResult(1)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setMaxResults(2)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expected);

        Path mockPath = mock(Path.class);
        Predicate mockPredicate = mock(Predicate.class);

        when(mockRoot.get("account")).thenReturn(mockPath);
        when(mockCriteriaBuilder.equal(mockPath, 3L)).thenReturn(mockPredicate);

        List<Purchase> result = purchaseDao.getClientPurchases(3L,2, 1);

        verify(mockCriteriaQuery).from(Purchase.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(mockPredicate);

        verify(mockEntityManager).createQuery(mockCriteriaQuery);
        verify(mockTypedQuery).setFirstResult(1);
        verify(mockTypedQuery).setMaxResults(2);

        assertEquals(expected, result);
    }

    @Test
    public void testGetProductPurchases() {
        List<Purchase> expected = new ArrayList<>();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Purchase> mockCriteriaQuery = (CriteriaQuery<Purchase>) mock(CriteriaQuery.class);
        TypedQuery<Purchase> mockTypedQuery = (TypedQuery<Purchase>) mock(TypedQuery.class);
        Root<Purchase> mockRoot = (Root<Purchase>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Purchase.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Purchase.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);

        when(mockTypedQuery.setFirstResult(1)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setMaxResults(2)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expected);

        Path mockPath = mock(Path.class);
        Predicate mockPredicate = mock(Predicate.class);

        when(mockRoot.get("product")).thenReturn(mockPath);
        when(mockCriteriaBuilder.equal(mockPath, 3L)).thenReturn(mockPredicate);

        List<Purchase> result = purchaseDao.getProductPurchases(3L,2, 1);

        verify(mockCriteriaQuery).from(Purchase.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(mockPredicate);

        verify(mockEntityManager).createQuery(mockCriteriaQuery);
        verify(mockTypedQuery).setFirstResult(1);
        verify(mockTypedQuery).setMaxResults(2);

        assertEquals(expected, result);
    }

    @Test
    public void testGetProductsPurchases() {
        List<Purchase> expected = new ArrayList<>();
        List<Long> products = Arrays.asList(1L, 2L);

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Purchase> mockCriteriaQuery = (CriteriaQuery<Purchase>) mock(CriteriaQuery.class);
        TypedQuery<Purchase> mockTypedQuery = (TypedQuery<Purchase>) mock(TypedQuery.class);
        Root<Purchase> mockRoot = (Root<Purchase>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Purchase.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Purchase.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);

        when(mockTypedQuery.setFirstResult(1)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setMaxResults(2)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expected);

        Path mockPath = mock(Path.class);
        Order mockOrder = mock(Order.class);
        Predicate mockPredicate = mock(Predicate.class);

        when(mockRoot.get("product")).thenReturn(mockPath);
        when(mockCriteriaBuilder.asc(mockPath)).thenReturn(mockOrder);
        when(mockPath.in(products)).thenReturn(mockPredicate);

        List<Purchase> result = purchaseDao.getProductsPurchases(products,2, 1);

        verify(mockCriteriaQuery).from(Purchase.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(mockPredicate);
        verify(mockCriteriaQuery).orderBy(mockOrder);

        verify(mockEntityManager).createQuery(mockCriteriaQuery);
        verify(mockTypedQuery).setFirstResult(1);
        verify(mockTypedQuery).setMaxResults(2);

        assertEquals(expected, result);
    }

    @Test
    public void testClear() {
        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaDelete<Purchase> mockCriteria = (CriteriaDelete<Purchase>) mock(CriteriaDelete.class);
        Query mockQuery = mock(Query.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createCriteriaDelete(Purchase.class)).thenReturn(mockCriteria);
        when(mockEntityManager.createQuery(mockCriteria)).thenReturn(mockQuery);

        purchaseDao.clear();

        verify(mockCriteria).from(Purchase.class);
        verify(mockEntityManager).createQuery(mockCriteria);
        verify(mockQuery).executeUpdate();
    }
}

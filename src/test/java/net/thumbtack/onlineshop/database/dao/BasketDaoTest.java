package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.AccountFactory;
import net.thumbtack.onlineshop.database.models.Basket;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class BasketDaoTest {

    private BasketDao basketDao;

    @Mock
    private EntityManager mockEntityManager;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        basketDao = new BasketDao(mockEntityManager);
    }

    @Test
    public void testInsert() {
        Basket basket = new Basket();

        basketDao.insert(basket);

        verify(mockEntityManager).persist(basket);
    }

    @Test
    public void testUpdate() {
        Basket basket = new Basket();

        basketDao.update(basket);

        verify(mockEntityManager).merge(basket);
    }

    @Test
    public void testDelete() {
        Basket basket = new Basket();
        when(mockEntityManager.merge(basket)).thenReturn(basket);

        basketDao.delete(basket);

        verify(mockEntityManager).merge(basket);
        verify(mockEntityManager).remove(basket);
    }

    @Test
    public void testGet() {
        Basket basket = new Basket();
        Account account = generateAccount();
        account.setId(1L);

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Basket> mockCriteriaQuery = (CriteriaQuery<Basket>) mock(CriteriaQuery.class);
        TypedQuery<Basket> mockTypedQuery = (TypedQuery<Basket>) mock(TypedQuery.class);
        Root<Basket> mockRoot = (Root<Basket>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Basket.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Basket.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(basket);

        Basket result = basketDao.get(account, 0);

        verify(mockCriteriaQuery).from(Basket.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(null, null, null);

        verify(mockRoot).get("account");
        verify(mockRoot).get("product");

        verify(mockCriteriaBuilder).equal(null, account.getId());
        verify(mockCriteriaBuilder).and();
        verify(mockCriteriaBuilder).equal(null, 0L);

        assertEquals(result, basket);
    }

    @Test
    public void testGetNull() {
        Account account = generateAccount();
        account.setId(1L);

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Basket> mockCriteriaQuery = (CriteriaQuery<Basket>) mock(CriteriaQuery.class);
        TypedQuery<Basket> mockTypedQuery = (TypedQuery<Basket>) mock(TypedQuery.class);
        Root<Basket> mockRoot = (Root<Basket>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Basket.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Basket.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        assertNull(basketDao.get(account, 0));

        verify(mockCriteriaQuery).from(Basket.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(null, null, null);

        verify(mockRoot).get("account");
        verify(mockRoot).get("product");

        verify(mockCriteriaBuilder).equal(null, account.getId());
        verify(mockCriteriaBuilder).and();
        verify(mockCriteriaBuilder).equal(null, 0L);
    }

    @Test
    public void testGetBasketList() {

        List<Basket> basket = Arrays.asList(
                new Basket(),
                new Basket()
        );
        Account account = generateAccount();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Basket> mockCriteriaQuery = (CriteriaQuery<Basket>) mock(CriteriaQuery.class);
        TypedQuery<Basket> mockTypedQuery = (TypedQuery<Basket>) mock(TypedQuery.class);
        Root<Basket> mockRoot = (Root<Basket>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Basket.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Basket.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(basket);

        List<Basket> result = basketDao.get(account);

        verify(mockCriteriaQuery).from(Basket.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        verify(mockRoot).get("account");

        verify(mockCriteriaBuilder).equal(null, account.getId());

        assertEquals(result.size(), basket.size());
    }

    private Account generateAccount() {
        return AccountFactory.createAdmin(
                "werew", "werwe", "wrew", "werwe", "werw", "sewr"
        );
    }

}

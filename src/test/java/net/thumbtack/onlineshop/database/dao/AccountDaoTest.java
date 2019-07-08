package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.AccountFactory;
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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AccountDaoTest {

    private AccountDao accountDao;

    @Mock
    private EntityManager mockEntityManager;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        accountDao = new AccountDao(mockEntityManager);
    }

    @Test
    public void testInsert() {
        Account account = generateAccount();
        accountDao.insert(account);

        verify(mockEntityManager).persist(account);
    }

    @Test
    public void testUpdate() {
        Account account = generateAccount();
        accountDao.update(account);

        verify(mockEntityManager).merge(account);
    }

    @Test
    public void testGet() {
        Account account = generateAccount();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Account> mockCriteriaQuery = (CriteriaQuery<Account>) mock(CriteriaQuery.class);
        TypedQuery<Account> mockTypedQuery = (TypedQuery<Account>) mock(TypedQuery.class);
        Root<Account> mockRoot = (Root<Account>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Account.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Account.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(account);

        Account result = accountDao.get("login", "password");

        // Проверяем что был условный селект
        verify(mockCriteriaQuery).from(Account.class);
        verify(mockCriteriaQuery).select(any());
        verify(mockCriteriaQuery).where(null, null, null);

        verify(mockRoot).get("login");
        verify(mockRoot).get("password");

        // Проверяем что реально была проверка логина и пароля
        verify(mockCriteriaBuilder).equal(null, "login");
        verify(mockCriteriaBuilder).and();
        verify(mockCriteriaBuilder).equal(null, "password");

        // А полученный аккунт реально из TypedQuery
        assertEquals(account, result);
    }

    @Test
    public void testNullGet() {

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Account> mockCriteriaQuery = (CriteriaQuery<Account>) mock(CriteriaQuery.class);
        TypedQuery<Account> mockTypedQuery = (TypedQuery<Account>) mock(TypedQuery.class);
        Root<Account> mockRoot = (Root<Account>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Account.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Account.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        assertNull(accountDao.get("login", "password"));

        // Проверяем что был условный селект
        verify(mockCriteriaQuery).from(Account.class);
        verify(mockCriteriaQuery).select(any());
        verify(mockCriteriaQuery).where(null, null, null);

        verify(mockRoot).get("login");
        verify(mockRoot).get("password");

        // Проверяем что реально была проверка логина и пароля
        verify(mockCriteriaBuilder).equal(null, "login");
        verify(mockCriteriaBuilder).and();
        verify(mockCriteriaBuilder).equal(null, "password");

    }

    @Test
    public void testExists() {
        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> mockCriteriaQuery = (CriteriaQuery<Long>) mock(CriteriaQuery.class);
        TypedQuery<Long> mockTypedQuery = (TypedQuery<Long>) mock(TypedQuery.class);
        Root<Account> mockRoot = (Root<Account>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Long.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Account.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(1L);

        assertTrue(accountDao.exists("login"));

        // Проверяем что был условный селект
        verify(mockCriteriaQuery).from(Account.class);
        verify(mockCriteriaQuery).select(any());
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        // Проверяем что реально была проверка логина
        verify(mockCriteriaBuilder).equal(null, "login");
    }

    @Test
    public void testNotExists() {

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> mockCriteriaQuery = (CriteriaQuery<Long>) mock(CriteriaQuery.class);
        TypedQuery<Long> mockTypedQuery = (TypedQuery<Long>) mock(TypedQuery.class);
        Root<Account> mockRoot = (Root<Account>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Long.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Account.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(0L);

        assertFalse(accountDao.exists("login"));

        // Проверяем что был условный селект
        verify(mockCriteriaQuery).from(Account.class);
        verify(mockCriteriaQuery).select(any());
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        // Проверяем что реально была проверка логина и пароля
        verify(mockCriteriaBuilder).equal(null, "login");
    }

    @Test
    public void testDelete() {
        Account account = generateAccount();
        when(mockEntityManager.merge(account)).thenReturn(account);

        accountDao.delete(account);

        verify(mockEntityManager).remove(account);
        verify(mockEntityManager).merge(account);
    }

    @Test
    public void testGetClients() {

        List<Account> list = Arrays.asList(generateAccount(), generateAccount());

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Account> mockCriteriaQuery = (CriteriaQuery<Account>) mock(CriteriaQuery.class);
        TypedQuery<Account> mockTypedQuery = (TypedQuery<Account>) mock(TypedQuery.class);
        Root<Account> mockRoot = (Root<Account>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Account.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Account.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(list);

        List<Account> result = accountDao.getClients();

        // Проверяем что был условный селект
        verify(mockCriteriaQuery).from(Account.class);
        verify(mockCriteriaQuery).select(any());
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        verify(mockCriteriaBuilder).equal(null, false);
    }

    private Account generateAccount() {
        return AccountFactory.createAdmin(
                "werew", "werwe", "wrew", "werwe", "werw", "sewr"
        );
    }
}

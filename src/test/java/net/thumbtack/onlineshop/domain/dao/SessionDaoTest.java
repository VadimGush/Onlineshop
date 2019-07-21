package net.thumbtack.onlineshop.domain.dao;

import net.thumbtack.onlineshop.domain.models.Session;
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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class SessionDaoTest {

    private SessionDao sessionDao;

    @Mock
    private EntityManager mockEntityManager;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        sessionDao = new SessionDao(mockEntityManager);
    }

    @Test
    public void testInsert() {
        Session session = new Session();

        sessionDao.insert(session);

        verify(mockEntityManager).persist(session);
    }

    @Test
    public void testDelete() {
        Session session = new Session();
        when(mockEntityManager.merge(session)).thenReturn(session);

        sessionDao.delete(session);

        verify(mockEntityManager).merge(session);
        verify(mockEntityManager).remove(session);
    }

    @Test
    public void testGet() {
        Session session = new Session();

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Session> mockCriteriaQuery = (CriteriaQuery<Session>) mock(CriteriaQuery.class);
        TypedQuery<Session> mockTypedQuery = (TypedQuery<Session>) mock(TypedQuery.class);
        Root<Session> mockRoot = (Root<Session>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Session.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Session.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(session);

        Session result = sessionDao.get("token");

        verify(mockCriteriaQuery).from(Session.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        verify(mockRoot).get("UUID");

        verify(mockCriteriaBuilder).equal(null, "token");

        assertEquals(session, result);

    }

    @Test
    public void testGetNull() {

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Session> mockCriteriaQuery = (CriteriaQuery<Session>) mock(CriteriaQuery.class);
        TypedQuery<Session> mockTypedQuery = (TypedQuery<Session>) mock(TypedQuery.class);
        Root<Session> mockRoot = (Root<Session>) mock(Root.class);

        when(mockEntityManager.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Session.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Session.class)).thenReturn(mockRoot);
        when(mockEntityManager.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        assertNull(sessionDao.get("token"));

        verify(mockCriteriaQuery).from(Session.class);
        verify(mockCriteriaQuery).select(mockRoot);
        verify(mockCriteriaQuery).where(nullable(Predicate.class));

        verify(mockRoot).get("UUID");

        verify(mockCriteriaBuilder).equal(null, "token");

    }

    @Test
    public void testClear() {

        Query mockQuery = mock(Query.class);
        when(mockEntityManager.createNativeQuery(any()))
                .thenReturn(mockQuery);

        sessionDao.clear();

        verify(mockEntityManager).createNativeQuery("delete from session");
        verify(mockQuery).executeUpdate();
    }
}

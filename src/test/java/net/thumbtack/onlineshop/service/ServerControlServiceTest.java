package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.Mockito.verify;

public class ServerControlServiceTest {

    @Mock
    private AccountDao mockAccountDao;

    @Mock
    private ProductDao mockProductDao;

    @Test
    public void testClear() {
        ServerControlService service;

        MockitoAnnotations.initMocks(this);
        service = new ServerControlService(Arrays.asList(mockAccountDao, mockProductDao));

        service.clear();

        verify(mockAccountDao).clear();
        verify(mockProductDao).clear();
    }
}

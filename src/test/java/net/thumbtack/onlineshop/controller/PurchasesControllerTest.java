package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.PurchasesDto;
import net.thumbtack.onlineshop.service.PurchasesService;
import net.thumbtack.onlineshop.service.ServiceException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

public class PurchasesControllerTest {

    private PurchasesController controller;

    @Mock
    private PurchasesService mockService;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        controller = new PurchasesController(mockService);
    }

    /**
     * Проверяем что по умолчанию всегда возвращается история покупок сгруппированная
     * по клиентам
     */
    @Test
    public void testGetPurchaseGetDefault() throws ServiceException {
        PurchasesDto expected = new PurchasesDto();

        List<Long> categories = Arrays.asList(1L, 2L, 3L);
        List<Long> ids = Arrays.asList(1L, 3L);

        when(mockService.getPurchases("token", PurchasesService.Target.CLIENT, 5, 10, ids, categories))
                .thenReturn(expected);

        PurchasesDto result = controller.getPurchases(
                "token", "werew", 5, 10, ids, categories);

        assertEquals(expected, result);
    }

    /**
     * История покупок сгруппированная по клиентам
     */
    @Test
    public void testGetPurchasesSortedByClients() throws ServiceException {
        PurchasesDto expected = new PurchasesDto();

        List<Long> categories = Arrays.asList(1L, 2L, 3L);
        List<Long> ids = Arrays.asList(1L, 3L);

        when(mockService.getPurchases("token", PurchasesService.Target.CLIENT, 5, 10, ids, categories))
                .thenReturn(expected);

        PurchasesDto result = controller.getPurchases(
                "token", "client", 5, 10, ids, categories);

        assertEquals(expected, result);
    }

    /**
     * История покупок сгруппированная по товарам
     */
    @Test
    public void testGetPurchasesSortedByProducts() throws ServiceException {
        PurchasesDto expected = new PurchasesDto();

        List<Long> categories = Arrays.asList(1L, 2L, 3L);
        List<Long> ids = Arrays.asList(1L, 3L);

        when(mockService.getPurchases("token", PurchasesService.Target.PRODUCT, 5, 10, ids, categories))
                .thenReturn(expected);

        PurchasesDto result = controller.getPurchases(
                "token", "product", 5, 10, ids, categories);

        assertEquals(expected, result);
    }

    /**
     * Проверим, что контроллер реально выбрасывает исключение
     */
    @Test(expected = ServiceException.class)
    public void testGetPurchasesServiceException() throws ServiceException {

        ServiceException exception = new ServiceException(ServiceException.ErrorCode.NOT_ADMIN);

        List<Long> categories = Arrays.asList(1L, 2L, 3L);
        List<Long> ids = Arrays.asList(1L, 3L);

        when(mockService.getPurchases("token", PurchasesService.Target.PRODUCT, 5, 10, ids, categories))
                .thenThrow(exception);

        try {
            controller.getPurchases("token", "product", 5, 10, ids, categories);
        } catch (ServiceException e) {
            assertEquals(exception, e);
            throw e;
        }

    }

}

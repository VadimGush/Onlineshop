package net.thumbtack.onlineshop.service.listeners;

import net.thumbtack.onlineshop.domain.models.Account;
import net.thumbtack.onlineshop.domain.models.Purchase;
import net.thumbtack.onlineshop.service.MailService;
import net.thumbtack.onlineshop.service.events.BasketPurchaseEvent;
import net.thumbtack.onlineshop.service.events.ProductPurchaseEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.Mockito.verify;

public class PurchaseEventListenerTest {

    private PurchaseEventListener listener;

    @Mock
    private MailService service;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        listener = new PurchaseEventListener(service);
    }

    /**
     * Отправка сообщения о покупке товара
     */
    @Test
    public void testMailClientAboutProductPurchase() {

        Purchase purchase = new Purchase();
        purchase.setId(3L);
        ProductPurchaseEvent event = new ProductPurchaseEvent(this, purchase);

        listener.mailClientAboutProductPurchase(event);

        verify(service).sendBuyProductReport(purchase);
    }

    /**
     * Отправка сообщения о покупке корзины
     */
    @Test
    public void testMailClientAboutBasketPurchase() {
        Account client = new Account();
        client.setId(4L);

        Purchase purchase1 = new Purchase();
        purchase1.setId(3L);

        Purchase purchase2 = new Purchase();
        purchase2.setId(1L);

        BasketPurchaseEvent event = new BasketPurchaseEvent(
                this, client, Arrays.asList(purchase1, purchase2));

        listener.mailClientAboutBasketPurchase(event);

        verify(service).sendBuyBasketReport(client, Arrays.asList(purchase1, purchase2));

    }
}

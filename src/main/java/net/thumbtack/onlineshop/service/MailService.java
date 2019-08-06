package net.thumbtack.onlineshop.service;

import kong.unirest.Unirest;
import net.thumbtack.onlineshop.domain.models.Account;
import net.thumbtack.onlineshop.domain.models.Product;
import net.thumbtack.onlineshop.domain.models.Purchase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис почтовых рассылок
 */
@Service
public class MailService {

    @Value("${mail_domain:#{null}}")
    private String mailDomain;

    @Value("${mail_api_key:#{null}}")
    private String mailApiKey;

    public MailService() {

    }

    /**
     * Отправляет сообщение на почту покупателя с отчётом о покупке товара
     *
     * @param purchase запись о покупке товара
     */
    public void sendBuyProductReport(Purchase purchase) {
        Product product = purchase.getProduct();

        // Отправляем одним письмом
        sendMessage(
                purchase.getAccount().getFirstName(),
                purchase.getAccount().getEmail(),
                "Уведомление о покупке товара",
                "<h4>Уведомление о покупке товара</h4>" +
                        createProductEntry(product.getName(), purchase.getCount(), purchase.getPrice()) +
                        "<hr>" +
                        "<b>Общая сумма:</b> " + (purchase.getCount() * purchase.getPrice()) + " руб." +
                        "<br><br>" +
                        "<b>Спасибо за покупку!</b>"
        );
    }

    /**
     * Отправляет сообщение на почту покупателя с отчётом о покупке списка товаров
     * из корзины
     *
     * @param client клиент, который совершил покупки
     * @param purchases список покупок
     */
    public void sendBuyBasketReport(Account client, List<Purchase> purchases) {
        // Формируем список покупок текстом
        StringBuilder purchaseList = new StringBuilder();

        // Посчитаем сумму всей покупки
        int sum = 0;

        for (Purchase entry : purchases) {
            purchaseList.append(
                    createProductEntry(entry.getProduct().getName(), entry.getCount(), entry.getPrice())
            );
            purchaseList.append("<hr>");

            sum += entry.getCount() * entry.getPrice();
        }

        // Отправляем всё одним письмом
        sendMessage(
                client.getFirstName(),
                client.getEmail(),
                "Уведомление о покупке товаров",
                "<h4>Уведомление о покупке товаров</h4>" +
                        purchaseList.toString() +
                        "<b>Общая сумма:</b> " + sum + " руб." +
                        "<br><br>" +
                        "<b>Спасибо за покупку!</b>"
        );

    }

    /**
     * Отправляет сообщение на почту, если параметры почтового домена и ключа API
     * были установлены.
     *
     * @param recipientName имя получателя
     * @param recipientAddress адрес получателя
     * @param title тема письма
     * @param content содержание письма (в HTML формате)
     */
    private void sendMessage(
            String recipientName,
            String recipientAddress,
            String title,
            String content) {

        // Если отправка почты не настроена
        if (mailDomain == null || mailApiKey == null)
            return;

        Unirest.post("https://api.mailgun.net/v3/" + mailDomain + "/messages")
                .basicAuth("api", mailApiKey)
                .queryString("from", "Notifications <notifications@onlineshop.net>")
                .queryString("to", recipientName +" <" + recipientAddress + ">")
                .queryString("subject", title)
                .queryString("html", content)
                .asJson();
    }

    /**
     * Формирует запись о покупке товара в виде строки
     *
     * @param name имя товара
     * @param count количество товара
     * @param price цена товара
     * @return запись о покупке в виде HTML строки
     */
    private String createProductEntry(String name, int count, int price) {
        return
                "<b>Товар:</b>" + name + "<br>" +
                "<b>Количество:</b> " + count + "<br>" +
                "<b>Цена за единицу:</b> " + price + "руб. <br>";
    }

}

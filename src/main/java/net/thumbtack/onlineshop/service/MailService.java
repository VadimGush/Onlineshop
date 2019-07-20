package net.thumbtack.onlineshop.service;

import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
     * Отправляет сообщение на почту, если параметры почтового домена и ключа API
     * были установлены.
     *
     * @param recipientName имя получателя
     * @param recipientAddress адрес получателя
     * @param title тема письма
     * @param content содержание письма (в HTML формате)
     */
    public void sendMessage(
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

}

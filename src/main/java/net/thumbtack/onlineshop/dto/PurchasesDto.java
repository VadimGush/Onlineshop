package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.thumbtack.onlineshop.domain.models.Purchase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchasesDto {

    // Ведомость
    private List<PurchaseDto> purchases = new ArrayList<>();

    // Сколько всего товара было выкуплено во всей выборке
    private Integer totalCount = 0;
    // На какую сумму было выкуплено товара в выборке
    private Integer totalAmount = 0;

    public void addPurchase(PurchaseDto purchase) {
        purchases.add(purchase);
        totalCount += purchase.getCount();
        totalAmount += purchase.getAmount();
    }

    public List<PurchaseDto> getPurchases() {
        return purchases;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    // Запись о покупке
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PurchaseDto {

        private static DateFormat format = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");

        // Id клиента, который совершил покупку
        private Long clientId;
        // Полное имя клиента
        private String clientFullName;

        // Id, товара, который был куплен
        private Long productId;
        // Имя товара
        private String productName;

        // Время покупки
        private String date;
        // Цена товара
        private Integer price;
        // Количество товаров
        private Integer count;
        // Сколько всего потрачено
        private Integer amount;

        /**
         * Полная запись о покупке товара
         *
         * @param purchase запись о покупке товара
         */
        public PurchaseDto(Purchase purchase) {
            this(purchase.getPrice(), purchase.getCount(), purchase.getDate());

            clientId = purchase.getAccount().getId();
            clientFullName = purchase.getAccount().getFullName();

            productId = purchase.getProduct().getId();
            productName = purchase.getProduct().getName();
        }

        private PurchaseDto(int price, int count, Date date) {
            this.price = price;
            this.count = count;
            this.date = format.format(date);

            amount = price * count;
        }

        public Long getClientId() {
            return clientId;
        }

        public String getClientFullName() {
            return clientFullName;
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public String getDate() {
            return date;
        }

        public Integer getPrice() {
            return price;
        }

        public Integer getCount() {
            return count;
        }

        public Integer getAmount() {
            return amount;
        }
    }


}

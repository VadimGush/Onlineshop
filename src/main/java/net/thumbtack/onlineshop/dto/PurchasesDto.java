package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.thumbtack.onlineshop.domain.models.Purchase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchasesDto {

    // Сколько всего записей было найдено по данным критериям
    private Long searchResults;
    // Ведомость
    private List<PurchaseDto> purchases = new ArrayList<>();
    // Сколько всего товара было выкуплено во всей выборке
    private Integer totalCount = 0;
    // На какую сумму было выкуплено товара в выборке
    private Integer totalAmount = 0;

    public PurchasesDto(Long searchResults) {
        this.searchResults = searchResults;
    }

    public void addPurchase(PurchaseDto purchase) {
        purchases.add(purchase);
        totalCount += purchase.getCount();
        totalAmount += purchase.getTotalAmount();
    }
    public Long getSearchResults() {
        return searchResults;
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

        // Id клиента, который совершил покупку
        private Long clientId;
        // Полное имя клиента
        private String clientFullName;

        // Id, товара, который был куплен
        private Long productId;
        // Имя товара
        private String productName;

        // Время покупки
        private Date date;
        // Цена товара
        private Integer price;
        // Количество товаров
        private Integer count;
        // Сколько всего потрачено
        private Integer totalAmount;

        public PurchaseDto(Purchase purchase) {
            clientId = purchase.getAccount().getId();
            clientFullName = purchase.getAccount().getFullName();

            productId = purchase.getProduct().getId();
            productName = purchase.getProduct().getName();

            date = purchase.getDate();
            price = purchase.getPrice();
            count = purchase.getCount();

            totalAmount = price * count;
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

        public Date getDate() {
            return date;
        }

        public Integer getPrice() {
            return price;
        }

        public Integer getCount() {
            return count;
        }

        public Integer getTotalAmount() {
            return totalAmount;
        }
    }


}

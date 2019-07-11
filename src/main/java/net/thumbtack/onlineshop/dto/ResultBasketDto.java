package net.thumbtack.onlineshop.dto;

import net.thumbtack.onlineshop.database.models.Basket;

import java.util.ArrayList;
import java.util.List;

public class ResultBasketDto {

    private List<BuyProductDto> bought;
    private List<BuyProductDto> remaining;

    public ResultBasketDto() {

    }

    public ResultBasketDto(List<BuyProductDto> bought, List<Basket> remaining) {

        this.bought = new ArrayList<>();
        this.bought.addAll(bought);

        this.remaining = new ArrayList<>();
        for (Basket basket : remaining) {
            this.remaining.add(
                    new BuyProductDto(
                            basket.getProduct().getId(),
                            basket.getProduct().getName(),
                            basket.getProduct().getPrice(),
                            basket.getCount()
                    )
            );
        }
    }

    public List<BuyProductDto> getBought() {
        return bought;
    }

    public void setBought(List<BuyProductDto> bought) {
        this.bought = bought;
    }

    public List<BuyProductDto> getRemaining() {
        return remaining;
    }

    public void setRemaining(List<BuyProductDto> remaining) {
        this.remaining = remaining;
    }
}

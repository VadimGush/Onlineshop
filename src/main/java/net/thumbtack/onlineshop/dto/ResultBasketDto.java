package net.thumbtack.onlineshop.dto;

import net.thumbtack.onlineshop.database.models.Basket;

import java.util.ArrayList;
import java.util.List;

public class ResultBasketDto {

    private List<ProductDto> bought;
    private List<ProductDto> remaining;

    public ResultBasketDto() {

    }

    public ResultBasketDto(List<ProductDto> bought, List<Basket> remaining) {

        this.bought = new ArrayList<>();
        this.bought.addAll(bought);

        this.remaining = new ArrayList<>();
        for (Basket basket : remaining) {
            this.remaining.add(
                    new ProductDto(
                            basket.getProduct().getId(),
                            basket.getProduct().getName(),
                            basket.getProduct().getPrice(),
                            basket.getCount()
                    )
            );
        }
    }

    public List<ProductDto> getBought() {
        return bought;
    }

    public void setBought(List<ProductDto> bought) {
        this.bought = bought;
    }

    public List<ProductDto> getRemaining() {
        return remaining;
    }

    public void setRemaining(List<ProductDto> remaining) {
        this.remaining = remaining;
    }
}

package net.thumbtack.onlineshop.response;

import java.util.List;

public class BasketBuyResponse {

    private List<ItemBuyResponse> bought;
    private List<ItemBuyResponse> remaining;

    public BasketBuyResponse(List<ItemBuyResponse> bought, List<ItemBuyResponse> remaining) {
        this.bought = bought;
        this.remaining = remaining;
    }

    public List<ItemBuyResponse> getBought() {
        return bought;
    }

    public void setBought(List<ItemBuyResponse> bought) {
        this.bought = bought;
    }

    public List<ItemBuyResponse> getRemaining() {
        return remaining;
    }

    public void setRemaining(List<ItemBuyResponse> remaining) {
        this.remaining = remaining;
    }
}

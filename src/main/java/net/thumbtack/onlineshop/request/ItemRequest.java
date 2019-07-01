package net.thumbtack.onlineshop.request;

import java.util.List;

/**
 * ItemRequest - запрос как на добавление/изменение товара
 */
public class ItemRequest {

    private String name;
    private int price;
    private int count;
    private List<Integer> categories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }
}

package net.thumbtack.onlineshop.dto;

import java.util.List;

public class ProductDto {

    private long id;

    private String name;
    private int price;
    private int count = 0;
    private List<Integer> categories;

    public ProductDto() {

    }


    public ProductDto(String name, int price, int count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public ProductDto(String name, int price, int count, List<Integer> categories) {
        this(name, price, count);
        this.categories = categories;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

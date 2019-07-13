package net.thumbtack.onlineshop.dto;

import java.util.List;

public class ProductEditDto {

    private String name;
    private Integer price;
    private Integer count;
    private List<Long> categories;

    public ProductEditDto() {
    }

    public ProductEditDto(String name, Integer price, Integer count, List<Long> categories) {
        this(name, price, count);
        this.categories = categories;
    }

    public ProductEditDto(String name, Integer price, Integer count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }
}

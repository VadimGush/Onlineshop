package net.thumbtack.onlineshop.dto;

public class BuyProductDto {

    private long id;
    private String name;
    private int price;
    private Integer count;

    public BuyProductDto() {

    }

    public BuyProductDto(long id, String name, int price, int count) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public BuyProductDto(long id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.count = 1;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}

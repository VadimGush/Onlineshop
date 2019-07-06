package net.thumbtack.onlineshop.database.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "int default 1")
    private Integer count;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer price;

    @ManyToOne
    private Product product;

    public Product() {

    }

    public Product(String name, Integer count, Integer price) {
        this.name = name;
        this.count = count;
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product1 = (Product) o;
        return Objects.equals(id, product1.id) &&
                Objects.equals(name, product1.name) &&
                Objects.equals(count, product1.count) &&
                Objects.equals(price, product1.price) &&
                Objects.equals(product, product1.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, count, price, product);
    }
}

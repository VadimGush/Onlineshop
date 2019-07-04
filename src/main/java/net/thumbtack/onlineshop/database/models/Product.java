package net.thumbtack.onlineshop.database.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Category> categories;

    @Column(nullable = false, columnDefinition = "int default 1")
    private Integer count;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer price;

    public Product() {

    }

    public Product(String name, Integer count, Integer price) {
        this.name = name;
        this.count = count;
        this.price = price;
    }

    public Product(String name, Integer count, Integer price, List<Category> categories) {
        this.name = name;
        this.count = count;
        this.price = price;
        this.categories = categories;
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

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

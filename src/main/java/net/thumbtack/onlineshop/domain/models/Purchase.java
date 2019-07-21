package net.thumbtack.onlineshop.domain.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Запись о покупке товара
 * <br>
 * Содержит в себе информацию о том кто купил, сколько товара и когда, а так же
 * по какой цене и в каком количестве. Используется затем для формирования сводной ведомости.
 */
@Entity
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private Integer price;

    public Purchase() {

    }

    public Purchase(Product product, Account account, Date date, int count, int price) {
        this.product = product;
        this.account = account;
        this.date = date;
        this.count = count;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase)) return false;
        Purchase history = (Purchase) o;
        return Objects.equals(id, history.id) &&
                Objects.equals(product, history.product) &&
                Objects.equals(account, history.account) &&
                Objects.equals(date, history.date) &&
                Objects.equals(count, history.count) &&
                Objects.equals(price, history.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, account, date, count, price);
    }
}

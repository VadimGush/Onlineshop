package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.thumbtack.onlineshop.controller.validation.OptionalNotBlank;
import net.thumbtack.onlineshop.controller.validation.RequiredName;
import net.thumbtack.onlineshop.database.models.Basket;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.database.models.ProductCategory;
import net.thumbtack.onlineshop.dto.actions.Edit;
import net.thumbtack.onlineshop.dto.actions.Register;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {

    private Long id;

    @RequiredName(groups = Register.class)
    @OptionalNotBlank(groups = Edit.class)
    private String name;

    @NotNull(groups = Register.class)
    @DecimalMin(value = "0", groups = { Register.class, Edit.class })
    private Integer price;

    @DecimalMin(value = "0", groups = { Register.class, Edit.class })
    private Integer count;

    private List<Long> categories;

    public ProductDto() {

    }

    public ProductDto(String name, Integer price, Integer count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public ProductDto(String name, Integer price, Integer count, List<Long> categories) {
        this(name, price, count);
        this.categories = categories;
    }

    public ProductDto(Long id, String name, Integer price, Integer count) {
        this(name, price, count);
        this.id = id;
    }

    public ProductDto(Long id, String name, Integer price, Integer count, List<Long> categories) {
        this(id, name, price, count);
        this.categories = categories;
    }

    public ProductDto(Basket basket) {
        this.id = basket.getProduct().getId();
        this.name = basket.getProduct().getName();
        this.price = basket.getProduct().getPrice();
        this.count = basket.getCount();
    }

    public ProductDto(Product product, List<ProductCategory> categories) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.count = product.getCount();

        if (categories != null && !categories.isEmpty()) {
            this.categories = new ArrayList<>();

            for (ProductCategory category : categories) {
                if (category.getCategory() != null)
                    this.categories.add(category.getCategory().getId());
            }
        }
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}

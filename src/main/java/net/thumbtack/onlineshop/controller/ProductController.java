package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.controller.validation.ValidationException;
import net.thumbtack.onlineshop.dto.ProductDto;
import net.thumbtack.onlineshop.dto.actions.Edit;
import net.thumbtack.onlineshop.dto.actions.Register;
import net.thumbtack.onlineshop.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("products")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto addProduct(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Validated(Register.class) ProductDto product,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        if (product.getCount() == null)
            product.setCount(0);

        return productService.add(session, product);
    }

    @PutMapping("products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto editProduct(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Validated(Edit.class) ProductDto product,
            BindingResult result,
            @PathVariable int id) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        return productService.edit(session, product, id);
    }

    @DeleteMapping("products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteProduct(
            @CookieValue("JAVASESSIONID") String session,
            @PathVariable int id) throws Exception {

        productService.delete(session, id);

        return "{}";
    }

    @GetMapping("products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProduct(
            @CookieValue("JAVASESSIONID") String session,
            @PathVariable int id) throws Exception {

        return productService.get(session, id);
    }

    @GetMapping("products")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> getProducts(
            @CookieValue("JAVASESSIONID") String session,
            @RequestParam(name = "category", required = false) List<Long> categories,
            @RequestParam(name = "order", required = false) String orderString) throws Exception {

        ProductService.SortOrder order = ProductService.SortOrder.PRODUCT;

        if (orderString != null && orderString.equals("category"))
            order = ProductService.SortOrder.CATEGORY;

        return productService.getAll(session, categories, order);
    }

}

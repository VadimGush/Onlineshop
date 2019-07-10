package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.controller.validation.ValidationException;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.dto.ProductDto;
import net.thumbtack.onlineshop.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
            @RequestBody @Valid ProductDto product,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        Product resultProduct = productService.add(session, product);

        return new ProductDto(
                resultProduct,
                productService.getCategories(session, product.getId())
        );
    }

    @PutMapping("products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String editProduct(@PathVariable int id) {
        return "{}";
    }

    @DeleteMapping("products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteProduct(@PathVariable int id) {
        return "{}";
    }

    @GetMapping("products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getProduct(@PathVariable int id) {
        return "{}";
    }

    @GetMapping("products")
    @ResponseStatus(HttpStatus.OK)
    public String getProducts() {
        return "{}";
    }

}

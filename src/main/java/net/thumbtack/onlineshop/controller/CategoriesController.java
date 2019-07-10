package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.controller.validation.ValidationException;
import net.thumbtack.onlineshop.database.models.Category;
import net.thumbtack.onlineshop.dto.CategoryDto;
import net.thumbtack.onlineshop.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api")
public class CategoriesController {

    private CategoriesService categoryService;

    @Autowired
    public CategoriesController(CategoriesService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("categories")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto addCategories(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Valid CategoryDto category,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        return new CategoryDto(categoryService.addCategory(session, category));
    }

    @GetMapping("categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoriesById(
            @PathVariable int id,
            @CookieValue("JAVASESSIONID") String session) throws Exception {

        return new CategoryDto(categoryService.getCategory(session, id));
    }

    @PutMapping("categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto editCategories(
            @CookieValue("JAVASESSIONID") String session,
            @PathVariable int id,
            @RequestBody @Valid CategoryDto category,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        return new CategoryDto(
                categoryService.editCategory(session, category, id)
        );
    }

    @DeleteMapping("categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteCategories(
            @CookieValue("JAVASESSIONID") String session,
            @PathVariable int id) throws Exception {

        categoryService.deleteCategory(session, id);

        return "{}";

    }

    @GetMapping("categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(
            @CookieValue("JAVASESSIONID") String session) throws Exception {

        List<Category> categories = categoryService.getCategories(session);
        List<CategoryDto> result = new ArrayList<>();

        categories.forEach((category) -> result.add(new CategoryDto(category)));

        return result;
    }
}

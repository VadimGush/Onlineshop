package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.thumbtack.onlineshop.controller.validation.RequiredName;
import net.thumbtack.onlineshop.database.models.Category;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto {

    private long id;

    @RequiredName
    private String name;

    private Long parentId;
    private String parentName;

    public CategoryDto() {

    }

    public CategoryDto(String name, long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public CategoryDto(Category category) {
        this.id = category.getId() == null ? 0 : category.getId();
        this.name = category.getName();
        if (category.getParent() != null) {
            this.parentId = category.getParent().getId();
            this.parentName = category.getParent().getName();
        }
    }

    public CategoryDto(String name) {
        this.name = name;
    }

    public CategoryDto(Long parentId) {
        this.parentId = parentId;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}

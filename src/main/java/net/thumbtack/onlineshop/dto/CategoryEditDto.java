package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.thumbtack.onlineshop.controller.validation.OptionalNotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryEditDto {

    @OptionalNotBlank
    private String name;
    private Long parentId;

    public CategoryEditDto() {
    }

    public CategoryEditDto(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public CategoryEditDto(String name) {
        this.name = name;
    }

    public CategoryEditDto(Long parentId) {
        this.parentId = parentId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

}

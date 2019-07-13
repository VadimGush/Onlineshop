package net.thumbtack.onlineshop.dto;

public class CategoryEditDto {

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

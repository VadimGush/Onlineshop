package net.thumbtack.onlineshop.dto;

public class CategoryDto {

    private long id;
    private String name;
    private Long parentId;
    private String parentName;

    public CategoryDto() {

    }

    public CategoryDto(String name, long parentId) {
        this.name = name;
        this.parentId = parentId;
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

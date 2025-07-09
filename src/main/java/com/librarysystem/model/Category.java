package com.librarysystem.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private int categoryID;
    private String categoryName;
    private Integer parentCategoryID; // Nullable for top-level categories
    private String description;   // Optional

    // For representing hierarchy
    private Category parentCategory;
    private List<Category> childCategories;

    public Category() {
        this.childCategories = new ArrayList<>();
    }

    public Category(String categoryName) {
        this();
        this.categoryName = categoryName;
    }

    // Getters and Setters
    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getParentCategoryID() {
        return parentCategoryID;
    }

    public void setParentCategoryID(Integer parentCategoryID) {
        this.parentCategoryID = parentCategoryID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
        if (parentCategory != null) {
            this.parentCategoryID = parentCategory.getCategoryID();
        }
    }

    public List<Category> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(List<Category> childCategories) {
        this.childCategories = childCategories;
    }

    public void addChildCategory(Category child) {
        if (this.childCategories == null) {
            this.childCategories = new ArrayList<>();
        }
        this.childCategories.add(child);
        child.setParentCategory(this);
    }


    @Override
    public String toString() {
        return "Category{" +
                "categoryID=" + categoryID +
                ", categoryName='" + categoryName + '\'' +
                (parentCategoryID != null ? ", parentCategoryID=" + parentCategoryID : "") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return categoryID == category.categoryID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(categoryID);
    }
}

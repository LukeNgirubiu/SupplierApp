package com.luke.supplierapp;

public class categoryItems {
    private String CategoryName;
    private String CategoryId;

    public categoryItems() {
    }

    public categoryItems(String categoryName, String categoryId) {
        CategoryName = categoryName;
        CategoryId = categoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }
}

package com.luke.supplierapp;

public class productSet {
    private String productName;
    private long productPrice;
    private long quantity;
    private String categoryId;
    private String productId;
    private String units;
    private String productPicture;

    public productSet() {
    }

    public productSet(String productName, long productPrice, long quantity, String categoryId, String productId, String units, String productPicture) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.productId = productId;
        this.units = units;
        this.productPicture = productPicture;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductPicture() {
        return productPicture;
    }

    public void setProductPicture(String productPicture) {
        this.productPicture = productPicture;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}

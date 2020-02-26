package com.luke.supplierapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class cartProductSet {
    private String productName;
    private long productPrice;
    private long quantity;
    private String categoryId;
    private long Total;
    private String productId;
    private String units;
    private String sellerId;
    @ServerTimestamp
    private Date date;

    public cartProductSet() {
    }

    public String getProductName() {
        return productName;
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

    public long getTotal() {
        return Total;
    }

    public void setTotal(long total) {
        Total = total;
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

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public cartProductSet(String productName, long productPrice, long quantity, String categoryId, long total, String productId, String units, String sellerId, Date date) {

        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.categoryId = categoryId;
        Total = total;
        this.productId = productId;
        this.units = units;
        this.sellerId = sellerId;
        this.date = date;
    }
}

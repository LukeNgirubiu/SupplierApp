package com.luke.supplierapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class setQrders {
    private String orderId;
    private boolean seen;
    private String supplierId;
    private String customerId;
    private long productPrice;
    private boolean notified;
    @ServerTimestamp
    Date date;
    private int seller;
    private int buyer;

    public setQrders() {
    }

    public setQrders(String orderId, boolean seen, String supplierId, String customerId, long productPrice, boolean notified, Date date, int seller, int buyer) {
        this.orderId = orderId;
        this.seen = seen;
        this.supplierId = supplierId;
        this.customerId = customerId;
        this.productPrice = productPrice;
        this.notified = notified;
        this.date = date;
        this.seller = seller;
        this.buyer = buyer;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public int getSeller() {
        return seller;
    }

    public void setSeller(int seller) {
        this.seller = seller;
    }

    public int getBuyer() {
        return buyer;
    }

    public void setBuyer(int buyer) {
        this.buyer = buyer;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

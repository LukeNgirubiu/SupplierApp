package com.luke.supplierapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class orders {
    private String supplierId;
    private String buyerId;
    private boolean seen;
    private long totalCost;
    @ServerTimestamp
   private Date date;

    public orders() {
    }

    public orders(String supplierId, String buyerId, boolean seen, long totalCost, Date date) {
        this.supplierId = supplierId;
        this.buyerId = buyerId;
        this.seen = seen;
        this.totalCost = totalCost;
        this.date = date;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}


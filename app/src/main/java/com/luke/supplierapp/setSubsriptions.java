package com.luke.supplierapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class setSubsriptions {
   private String buyerId;
   @ServerTimestamp
   private Date date;
   private String id;
   private boolean seen;

    public setSubsriptions() {
    }

    public setSubsriptions(String buyerId, Date date, String id, boolean seen) {
        this.buyerId = buyerId;
        this.date = date;
        this.id = id;
        this.seen = seen;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

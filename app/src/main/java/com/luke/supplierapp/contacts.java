package com.luke.supplierapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class contacts {
    private String contact;
    @ServerTimestamp
    private Date date;
private  String userId;

    public contacts(String contact, Date date, String userId) {
        this.contact = contact;
        this.date = date;
        this.userId = userId;
    }

    public contacts() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

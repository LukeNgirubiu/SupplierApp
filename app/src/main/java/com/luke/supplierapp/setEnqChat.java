package com.luke.supplierapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class setEnqChat {
    private String usedId;
    @ServerTimestamp
    private Date date;

    public setEnqChat() {
    }

    public setEnqChat(String usedId, Date date) {
        this.usedId = usedId;
        this.date = date;
    }

    public String getUsedId() {
        return usedId;
    }

    public void setUsedId(String usedId) {
        this.usedId = usedId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

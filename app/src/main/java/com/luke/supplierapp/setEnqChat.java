package com.luke.supplierapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class setEnqChat {
    private String usedId;
    private int seen;
    @ServerTimestamp
    private Date date;

    public setEnqChat() {
    }


    public setEnqChat(String usedId, int seen, Date date) {
        this.usedId = usedId;
        this.seen = seen;
        this.date = date;
    }

    public String getUsedId() {
        return usedId;
    }

    public void setUsedId(String usedId) {
        this.usedId = usedId;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

package com.luke.supplierapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class setEnquire {
    private String fromId;
    private String toId;
    private String message;
    private String enquiryId;
    @ServerTimestamp
    Date date;



    public setEnquire() {
    }

    public setEnquire(String fromId, String toId, String message, String enquiryId, Date date) {
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;
        this.enquiryId = enquiryId;
        this.date = date;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEnquiryId() {
        return enquiryId;
    }

    public void setEnquiryId(String enquiryId) {
        this.enquiryId = enquiryId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

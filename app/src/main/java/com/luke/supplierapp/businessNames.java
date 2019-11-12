package com.luke.supplierapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class businessNames {
    private String businessName;
    private String slogan;
    @ServerTimestamp
   private Date date;


    public businessNames() {
    }

    public businessNames(String businessName, String slogan, Date date) {
        this.businessName = businessName;
        this.slogan = slogan;
        this.date = date;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

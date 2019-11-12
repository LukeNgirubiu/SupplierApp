package com.luke.supplierapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class userDetails {
    private String firstName;
    private String secondName;
    private String surName;
    private Double latitude;
    private Double longitude;
    private String imagePath;
    private int type;
    String id;
    private String contact;
    @ServerTimestamp
    Date date;

    public userDetails() {
    }

    public userDetails(String firstName, String secondName, String surName, Double latitude, Double longitude, String imagePath, int type, String id, String contact, Date date) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.surName = surName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imagePath = imagePath;
        this.type = type;
        this.id = id;
        this.contact = contact;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

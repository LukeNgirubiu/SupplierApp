package com.luke.supplierapp;

public class categorySet {
    private String supplierId;
    private Double latitude;
    private Double longitude;

    public categorySet(String supplierId, Double latitude, Double longitude) {
        this.supplierId = supplierId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public categorySet() {
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
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
}

package com.luke.supplierapp;

public class Images {
    String imageString;
String productId;
String imgeId;

    public Images() {
    }

    public Images(String imageString, String productId, String imgeId) {
        this.imageString = imageString;
        this.productId = productId;
        this.imgeId = imgeId;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImgeId() {
        return imgeId;
    }

    public void setImgeId(String imgeId) {
        this.imgeId = imgeId;
    }
}

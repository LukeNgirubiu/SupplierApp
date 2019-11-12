package com.luke.supplierapp;

public class cellContacts {
    private String cellNo;
    private String id;

    public cellContacts() {
    }

    public cellContacts(String cellNo, String id) {
        this.cellNo = cellNo;
        this.id = id;
    }

    public String getCellNo() {
        return cellNo;
    }

    public void setCellNo(String cellNo) {
        this.cellNo = cellNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

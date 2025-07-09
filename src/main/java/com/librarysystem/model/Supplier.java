package com.librarysystem.model;

public class Supplier {
    private int supplierID;
    private String supplierName;
    private String contactPerson;
    private String address;
    private String phoneNumber;
    private String email;
    private String paymentTerms;

    // Default constructor
    public Supplier() {
    }

    // Parameterized constructor
    public Supplier(String supplierName, String contactPerson, String address, String phoneNumber, String email, String paymentTerms) {
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.paymentTerms = paymentTerms;
    }

    // Getters and Setters
    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierID=" + supplierID +
                ", supplierName='" + supplierName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

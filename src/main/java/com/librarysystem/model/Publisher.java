package com.librarysystem.model;

public class Publisher {
    private int publisherID;
    private String publisherName;
    private String address; // Optional
    private String website; // Optional

    public Publisher() {
    }

    public Publisher(String publisherName) {
        this.publisherName = publisherName;
    }

    // Getters and Setters
    public int getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(int publisherID) {
        this.publisherID = publisherID;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "Publisher{" +
                "publisherID=" + publisherID +
                ", publisherName='" + publisherName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Publisher publisher = (Publisher) o;
        return publisherID == publisher.publisherID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(publisherID);
    }
}

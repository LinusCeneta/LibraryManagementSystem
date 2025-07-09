package com.librarysystem.model;

// Represents a Branch or a specific location within the library system
public class Location {
    private int locationID;
    private String branchName;
    private String address;
    private String phoneNumber;
    private String operatingHours;

    public Location() {
    }

    public Location(String branchName) {
        this.branchName = branchName;
    }

    // Getters and Setters
    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
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

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    @Override
    public String toString() {
        return "Location{" +
                "locationID=" + locationID +
                ", branchName='" + branchName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return locationID == location.locationID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(locationID);
    }
}

package com.librarysystem.model;

public class ItemType {
    private int itemTypeID;
    private String typeName; // e.g., Book, DVD, Magazine, Reference
    private boolean isLoanable;

    public ItemType() {
    }

    public ItemType(String typeName, boolean isLoanable) {
        this.typeName = typeName;
        this.isLoanable = isLoanable;
    }

    // Getters and Setters
    public int getItemTypeID() {
        return itemTypeID;
    }

    public void setItemTypeID(int itemTypeID) {
        this.itemTypeID = itemTypeID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isLoanable() {
        return isLoanable;
    }

    public void setLoanable(boolean loanable) {
        isLoanable = loanable;
    }

    @Override
    public String toString() {
        return "ItemType{" +
                "itemTypeID=" + itemTypeID +
                ", typeName='" + typeName + '\'' +
                ", isLoanable=" + isLoanable +
                '}';
    }
}

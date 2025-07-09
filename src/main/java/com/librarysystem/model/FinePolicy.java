package com.librarysystem.model;

import java.math.BigDecimal;

public class FinePolicy {
    private int policyID;
    private Integer itemTypeID; // Nullable, for general policies
    private String policyName;
    private BigDecimal overdueFinePerDay;
    private BigDecimal maxFineAmount; // Max overdue fine per loan item
    private BigDecimal lostItemFeeFixed; // Fixed fee for lost items
    private BigDecimal lostItemFeePercentage; // Percentage of item's cost
    private int gracePeriodDays;

    // Optional: Store ItemType object
    private ItemType itemType;

    public FinePolicy() {
    }

    // Getters and Setters
    public int getPolicyID() {
        return policyID;
    }

    public void setPolicyID(int policyID) {
        this.policyID = policyID;
    }

    public Integer getItemTypeID() {
        return itemTypeID;
    }

    public void setItemTypeID(Integer itemTypeID) {
        this.itemTypeID = itemTypeID;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public BigDecimal getOverdueFinePerDay() {
        return overdueFinePerDay;
    }

    public void setOverdueFinePerDay(BigDecimal overdueFinePerDay) {
        this.overdueFinePerDay = overdueFinePerDay;
    }

    public BigDecimal getMaxFineAmount() {
        return maxFineAmount;
    }

    public void setMaxFineAmount(BigDecimal maxFineAmount) {
        this.maxFineAmount = maxFineAmount;
    }

    public BigDecimal getLostItemFeeFixed() {
        return lostItemFeeFixed;
    }

    public void setLostItemFeeFixed(BigDecimal lostItemFeeFixed) {
        this.lostItemFeeFixed = lostItemFeeFixed;
    }

    public BigDecimal getLostItemFeePercentage() {
        return lostItemFeePercentage;
    }

    public void setLostItemFeePercentage(BigDecimal lostItemFeePercentage) {
        this.lostItemFeePercentage = lostItemFeePercentage;
    }

    public int getGracePeriodDays() {
        return gracePeriodDays;
    }

    public void setGracePeriodDays(int gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
        if(itemType != null) this.itemTypeID = itemType.getItemTypeID();
    }

    @Override
    public String toString() {
        return "FinePolicy{" +
                "policyID=" + policyID +
                ", policyName='" + policyName + '\'' +
                ", overdueFinePerDay=" + overdueFinePerDay +
                '}';
    }
}

package com.librarysystem.dto;

import java.math.BigDecimal;

public class InventoryValuationDTO {
    private String dimension; // e.g., Category Name, Branch Name, Condition
    private long itemCount;
    private BigDecimal totalValue;

    public InventoryValuationDTO(String dimension, long itemCount, BigDecimal totalValue) {
        this.dimension = dimension;
        this.itemCount = itemCount;
        this.totalValue = totalValue;
    }

    // Getters
    public String getDimension() {
        return dimension;
    }

    public long getItemCount() {
        return itemCount;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    // Setters (optional)
}

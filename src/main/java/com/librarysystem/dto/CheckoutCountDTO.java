package com.librarysystem.dto;

// DTO for Checkout Counts Report
public class CheckoutCountDTO {
    private String dimension; // e.g., Date, Branch Name, Category Name, Format
    private long count;

    public CheckoutCountDTO(String dimension, long count) {
        this.dimension = dimension;
        this.count = count;
    }

    // Getters
    public String getDimension() {
        return dimension;
    }

    public long getCount() {
        return count;
    }

    // Setters (optional, if needed)
    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "CheckoutCountDTO{" +
                "dimension='" + dimension + '\'' +
                ", count=" + count +
                '}';
    }
}

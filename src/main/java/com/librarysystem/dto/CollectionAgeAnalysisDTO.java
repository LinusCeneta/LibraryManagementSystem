package com.librarysystem.dto;

public class CollectionAgeAnalysisDTO {
    private String ageGroup; // e.g., "0-1 Years", "1-3 Years", "Older than X Years"
    private long itemCount;
    private double percentageOfCollection;

    public CollectionAgeAnalysisDTO(String ageGroup, long itemCount, double percentageOfCollection) {
        this.ageGroup = ageGroup;
        this.itemCount = itemCount;
        this.percentageOfCollection = percentageOfCollection;
    }

    // Getters
    public String getAgeGroup() {
        return ageGroup;
    }

    public long getItemCount() {
        return itemCount;
    }

    public double getPercentageOfCollection() {
        return percentageOfCollection;
    }

    // Setters if needed
}

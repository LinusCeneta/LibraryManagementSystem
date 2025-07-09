package com.librarysystem.dto;

import java.math.BigDecimal;

public class FineRevenueDTO {
    private BigDecimal totalFinesCollected;
    private long numberOfFinesPaid; // Number of distinct fine transactions paid
    private BigDecimal averageFineAmount; // Average amount per paid fine transaction

    // Could also include:
    // private String periodDimension; // e.g., "YYYY-MM" if grouping by month

    public FineRevenueDTO(BigDecimal totalFinesCollected, long numberOfFinesPaid) {
        this.totalFinesCollected = totalFinesCollected != null ? totalFinesCollected : BigDecimal.ZERO;
        this.numberOfFinesPaid = numberOfFinesPaid;
        if (numberOfFinesPaid > 0 && this.totalFinesCollected.compareTo(BigDecimal.ZERO) > 0) {
            this.averageFineAmount = this.totalFinesCollected.divide(BigDecimal.valueOf(numberOfFinesPaid), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.averageFineAmount = BigDecimal.ZERO;
        }
    }

    // Getters
    public BigDecimal getTotalFinesCollected() {
        return totalFinesCollected;
    }

    public long getNumberOfFinesPaid() {
        return numberOfFinesPaid;
    }

    public BigDecimal getAverageFineAmount() {
        return averageFineAmount;
    }

    // Setters (optional)
}

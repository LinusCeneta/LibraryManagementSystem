package com.librarysystem.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrder {
    private int poID;
    private String poNumber;
    private int supplierID;
    private Date orderDate;
    private Date expectedDeliveryDate;
    private String status; // e.g., Created, Submitted, Partially Received, Fully Received, Closed
    private BigDecimal totalAmount;
    private int createdBy; // UserID of staff
    private List<PurchaseOrderLine> orderLines;

    // Constructors
    public PurchaseOrder() {
        this.orderLines = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
    }

    // Getters and Setters
    public int getPoID() {
        return poID;
    }

    public void setPoID(int poID) {
        this.poID = poID;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void calculateTotalAmount() {
        this.totalAmount = BigDecimal.ZERO;
        if (orderLines != null) {
            for (PurchaseOrderLine line : orderLines) {
                this.totalAmount = this.totalAmount.add(line.getLineTotal());
            }
        }
    }


    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public List<PurchaseOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<PurchaseOrderLine> orderLines) {
        this.orderLines = orderLines;
        calculateTotalAmount(); // Recalculate when lines are set
    }

    public void addOrderLine(PurchaseOrderLine line) {
        if (this.orderLines == null) {
            this.orderLines = new ArrayList<>();
        }
        this.orderLines.add(line);
        line.setPoID(this.poID); // Ensure the line item knows its parent PO ID
        calculateTotalAmount(); // Recalculate when a line is added
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "poID=" + poID +
                ", poNumber='" + poNumber + '\'' +
                ", supplierID=" + supplierID +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}

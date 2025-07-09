package model;

import java.sql.Date;

public class PurchaseOrder {
    private int poId;
    private String poNumber;
    private int supplierId;
    private Date createdDate;
    private Date expectedDeliveryDate;
    private int requestId;
    private String status; // e.g., "Created", "Submitted", "Partially Received", "Fully Received", "Closed"

    public PurchaseOrder() {}

    public PurchaseOrder(int poId, String poNumber, int supplierId, Date createdDate, 
            Date expectedDeliveryDate, String status, int requestId) {
        this.poId = poId;
        this.poNumber = poNumber;
        this.supplierId = supplierId;
        this.createdDate = createdDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.status = status;
        this.requestId = requestId;
    }

    public int getPoId() { return poId; }
    public void setPoId(int poId) { this.poId = poId; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Date getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(Date expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
}

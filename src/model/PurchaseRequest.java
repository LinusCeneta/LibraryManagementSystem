package model;

import java.sql.Date;

public class PurchaseRequest {
    private int requestId;
    private String title;
    private String requestedBy;
    private Date requestDate;
    private String status; // e.g., "Pending", "Approved", "Rejected"

    public PurchaseRequest() {}

    public PurchaseRequest(int requestId, String title, String requestedBy, Date requestDate, String status) {
        this.requestId = requestId;
        this.title = title;
        this.requestedBy = requestedBy;
        this.requestDate = requestDate;
        this.status = status;
    }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

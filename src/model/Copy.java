package model;

import java.util.Date;

public class Copy {
    private String copyId; // barcode or RFID
    private String isbn;   // FK to Book.isbn
    private Date acquisitionDate;
    private double cost;
    private Condition condition;
    private String location;
    private Status status;

    public Copy() {}

    public Copy(String copyId, String isbn, Date acquisitionDate, double cost, Condition condition, String location, Status status) {
        this.copyId = copyId;
        this.isbn = isbn;
        this.acquisitionDate = acquisitionDate;
        this.cost = cost;
        this.condition = condition;
        this.location = location;
        this.status = status;
    }

    public String getCopyId() { return copyId; }
    public void setCopyId(String copyId) { this.copyId = copyId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Date getAcquisitionDate() { return acquisitionDate; }
    public void setAcquisitionDate(Date acquisitionDate) { this.acquisitionDate = acquisitionDate; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}

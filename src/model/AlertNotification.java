package model;

import java.sql.Date;

public class AlertNotification {
    private String type;
    private int itemId;
    private String title;
    private String memberName;
    private Date date;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}

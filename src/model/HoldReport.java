package model;

public class HoldReport {
    private String title;
    private int holdQueue;
    private double avgWaitDays;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getHoldQueue() {
        return holdQueue;
    }
    public void setHoldQueue(int holdQueue) {
        this.holdQueue = holdQueue;
    }

    public double getAvgWaitDays() {
        return avgWaitDays;
    }
    public void setAvgWaitDays(double avgWaitDays) {
        this.avgWaitDays = avgWaitDays;
    }
}


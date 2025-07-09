package model;

import java.util.Map;

public class BranchOverviewReport {
    private int totalItems;
    private int availableItems;
    private int checkedOutItems;
    private int onHoldItems;
    private int dailyCheckouts;
    private int dailyReturns;
    private int overdueCount;
    private Map<String, Integer> lowInventoryAlerts;

    // Getters & Setters

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public int getAvailableItems() { return availableItems; }
    public void setAvailableItems(int availableItems) { this.availableItems = availableItems; }

    public int getCheckedOutItems() { return checkedOutItems; }
    public void setCheckedOutItems(int checkedOutItems) { this.checkedOutItems = checkedOutItems; }

    public int getOnHoldItems() { return onHoldItems; }
    public void setOnHoldItems(int onHoldItems) { this.onHoldItems = onHoldItems; }

    public int getDailyCheckouts() { return dailyCheckouts; }
    public void setDailyCheckouts(int dailyCheckouts) { this.dailyCheckouts = dailyCheckouts; }

    public int getDailyReturns() { return dailyReturns; }
    public void setDailyReturns(int dailyReturns) { this.dailyReturns = dailyReturns; }

    public int getOverdueCount() { return overdueCount; }
    public void setOverdueCount(int overdueCount) { this.overdueCount = overdueCount; }

    public Map<String, Integer> getLowInventoryAlerts() { return lowInventoryAlerts; }
    public void setLowInventoryAlerts(Map<String, Integer> lowInventoryAlerts) { this.lowInventoryAlerts = lowInventoryAlerts; }
}

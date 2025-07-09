package model;

import java.util.List;

public class StaffTaskReport {
    private int pendingAcquisitions;
    private int booksToCatalog;
    private List<Item> itemsToCatalog; // Detailed list
    private int itemsAwaitingCheckIn;
    private List<Item> itemsForCheckIn; // Detailed list
    private int holdsToProcess;

    // Getters and setters for counts
    public int getPendingAcquisitions() { return pendingAcquisitions; }
    public void setPendingAcquisitions(int pendingAcquisitions) { this.pendingAcquisitions = pendingAcquisitions; }

    public int getBooksToCatalog() { return booksToCatalog; }
    public void setBooksToCatalog(int booksToCatalog) { this.booksToCatalog = booksToCatalog; }

    public int getItemsAwaitingCheckIn() { return itemsAwaitingCheckIn; }
    public void setItemsAwaitingCheckIn(int itemsAwaitingCheckIn) { this.itemsAwaitingCheckIn = itemsAwaitingCheckIn; }

    public int getHoldsToProcess() { return holdsToProcess; }
    public void setHoldsToProcess(int holdsToProcess) { this.holdsToProcess = holdsToProcess; }


    

    public List<Item> getItemsToCatalog() { return itemsToCatalog; }
    public void setItemsToCatalog(List<Item> itemsToCatalog) { 
        this.itemsToCatalog = itemsToCatalog; 
    }

    public List<Item> getItemsForCheckIn() { return itemsForCheckIn; }
    public void setItemsForCheckIn(List<Item> itemsForCheckIn) { 
        this.itemsForCheckIn = itemsForCheckIn; 
    }


}
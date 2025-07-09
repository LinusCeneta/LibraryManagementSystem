package com.librarysystem.dto;

public class TopCirculatingItemDTO {
    private int bookId;
    private String bookTitle;
    private String isbn;
    private long checkoutCount;

    public TopCirculatingItemDTO(int bookId, String bookTitle, String isbn, long checkoutCount) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.isbn = isbn;
        this.checkoutCount = checkoutCount;
    }

    // Getters
    public int getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getIsbn() {
        return isbn;
    }

    public long getCheckoutCount() {
        return checkoutCount;
    }

    // Setters if needed
}

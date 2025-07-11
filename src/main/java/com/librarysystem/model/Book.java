// Placeholder Book.java model class
package com.librarysystem.model;

import java.util.List;

public class Book {
    private int bookID;
    private String isbn;
    private String title;
    private String subtitle;
    // private List<Author> authors; // Or String authors if simplified
    private String authorsConcatenated; // Simple way for now
    // private Publisher publisher; // Or String publisherName
    private String publisherName; // Simple way for now
    private int publicationYear;
    private String edition;
    private String language;
    // private List<Category> categories;
    private String categoriesConcatenated; // Simple way for now
    private String deweyDecimal;
    private String locCallNumber; // Library of Congress Call Number
    private int numberOfPages;
    private String summary;
    private String coverImageURL;

    public Book() {
    }

    // Getters and Setters
    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAuthorsConcatenated() {
        return authorsConcatenated;
    }

    public void setAuthorsConcatenated(String authorsConcatenated) {
        this.authorsConcatenated = authorsConcatenated;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCategoriesConcatenated() {
        return categoriesConcatenated;
    }

    public void setCategoriesConcatenated(String categoriesConcatenated) {
        this.categoriesConcatenated = categoriesConcatenated;
    }

    public String getDeweyDecimal() {
        return deweyDecimal;
    }

    public void setDeweyDecimal(String deweyDecimal) {
        this.deweyDecimal = deweyDecimal;
    }

    public String getLocCallNumber() {
        return locCallNumber;
    }

    public void setLocCallNumber(String locCallNumber) {
        this.locCallNumber = locCallNumber;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoverImageURL() {
        return coverImageURL;
    }

    public void setCoverImageURL(String coverImageURL) {
        this.coverImageURL = coverImageURL;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookID=" + bookID +
                ", title='" + title + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }

    // Fields for search/detail display
    private Publisher publisher; // Object form of PublisherID
    private List<Author> bookAuthors;
    private List<Category> bookCategories;
    private List<Copy> availableCopies; // Summary of available copies, perhaps per location

    public Book() {
        this.bookAuthors = new ArrayList<>();
        this.bookCategories = new ArrayList<>();
        this.availableCopies = new ArrayList<>();
    }


    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
        if (publisher != null) this.publisherName = publisher.getPublisherName(); // Keep simple field in sync
    }

    public List<Author> getBookAuthors() {
        return bookAuthors;
    }

    public void setBookAuthors(List<Author> bookAuthors) {
        this.bookAuthors = bookAuthors;
        // Optionally update authorsConcatenated from this list
    }

    public void addAuthor(Author author) {
        if (this.bookAuthors == null) this.bookAuthors = new ArrayList<>();
        this.bookAuthors.add(author);
    }

    public List<Category> getBookCategories() {
        return bookCategories;
    }

    public void setBookCategories(List<Category> bookCategories) {
        this.bookCategories = bookCategories;
         // Optionally update categoriesConcatenated from this list
    }

    public void addCategory(Category category) {
        if (this.bookCategories == null) this.bookCategories = new ArrayList<>();
        this.bookCategories.add(category);
    }

    public List<Copy> getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(List<Copy> availableCopies) {
        this.availableCopies = availableCopies;
    }
}

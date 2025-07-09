package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class Book {
    public enum Format {
        HARDCOVER,
        PAPERBACK,
        EBOOK,
        AUDIOBOOK
    }

    // Fields with initialization
    private String id; // UUID as String
    private String isbn;
    private String title;
    private String subtitle;
    private String authorsAsString; // For form input
    private List<Author> authors = new ArrayList<>();
    private String categoriesAsString; // For form input
    private List<Category> categories = new ArrayList<>();
    private int publicationYear;
    private String edition;
    private String language;
    private String callNumber;
    private int numberOfPages;
    private String summary;
    private Format format;
    private Publisher publisher;
    

    // Constructors
    public Book() {
        this.id = UUID.randomUUID().toString(); // Auto-generate UUID on creation
    }

    public Book(String isbn, String title, String subtitle, List<Author> authors,
               Publisher publisher, int publicationYear, String edition,
               String language, List<Category> categories, String callNumber,
               int numberOfPages, String summary, Format format) {
        this();
        this.isbn = isbn;
        this.title = title;
        this.subtitle = subtitle;
        setAuthors(authors);
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.edition = edition;
        this.language = language;
        setCategories(categories);
        this.callNumber = callNumber;
        this.numberOfPages = numberOfPages;
        this.summary = summary;
        this.format = format;
    }

    // ID methods
    public String getId() {
        return this.id;
    }

    protected void setId(String id) {
        if (this.id == null && id != null) {
            this.id = id;
        }
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        this.id = String.valueOf(id);
    }

    // Format methods
    public Format getFormat() { 
        return format; 
    }

    public void setFormat(Format format) {
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        this.format = format;
    }

    public void setFormat(String formatStr) {
        try {
            this.format = Format.valueOf(formatStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid book format: " + formatStr + 
                ". Valid formats are: " + Arrays.toString(Format.values())
            );
        }
    }

    // Standard getters and setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { 
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
        this.isbn = isbn; 
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { 
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title; 
    }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) {
        int currentYear = java.time.Year.now().getValue();
        if (publicationYear < 1800 || publicationYear > currentYear) {
            throw new IllegalArgumentException(
                "Publication year must be between 1800 and " + currentYear
            );
        }
        this.publicationYear = publicationYear;
    }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { 
        if (language != null && language.length() > 50) {
            throw new IllegalArgumentException("Language cannot exceed 50 characters");
        }
        this.language = language; 
    }

    public String getCallNumber() { return callNumber; }
    public void setCallNumber(String callNumber) { 
        if (callNumber != null && callNumber.length() > 20) {
            throw new IllegalArgumentException("Call number cannot exceed 20 characters");
        }
        this.callNumber = callNumber; 
    }

    public int getNumberOfPages() { return numberOfPages; }
    public void setNumberOfPages(int numberOfPages) {
        if (numberOfPages <= 0) {
            throw new IllegalArgumentException("Number of pages must be positive");
        }
        this.numberOfPages = numberOfPages;
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { 
        if (summary != null && summary.length() > 2000) {
            throw new IllegalArgumentException("Summary cannot exceed 2000 characters");
        }
        this.summary = summary; 
    }

    public Publisher getPublisher() { return publisher; }
    public void setPublisher(Publisher publisher) { this.publisher = publisher; }

    // Authors methods
    public List<Author> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors != null ? new ArrayList<>(authors) : new ArrayList<>();
        updateAuthorsAsString();
    }

    public String getAuthorsAsString() {
        if (!authors.isEmpty()) {
            return authors.stream()
                .filter(Objects::nonNull)
                .map(Author::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
        }
        return authorsAsString != null ? authorsAsString : "";
    }

    public void setAuthorsAsString(String authorsAsString) {
        this.authorsAsString = authorsAsString;
    }

    private void updateAuthorsAsString() {
        this.authorsAsString = getAuthorsAsString();
    }

    // Author management methods
    public boolean hasAuthor(Author author) {
        if (author == null) return false;
        return authors.stream()
            .anyMatch(a -> a != null && a.getId() == author.getId());
    }

    public void addAuthor(Author author) {
        if (author == null) return;
        if (!hasAuthor(author)) {
            authors.add(author);
            updateAuthorsAsString();
        }
    }

    public void removeAuthor(Author author) {
        if (author == null) return;
        authors.removeIf(a -> a != null && a.getId() == author.getId());
        updateAuthorsAsString();
    }

    // Categories methods
    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories != null ? new ArrayList<>(categories) : new ArrayList<>();
        updateCategoriesAsString();
    }

    public String getCategoriesAsString() {
        if (!categories.isEmpty()) {
            return categories.stream()
                .filter(Objects::nonNull)
                .map(Category::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
        }
        return categoriesAsString != null ? categoriesAsString : "";
    }

    public void setCategoriesAsString(String categoriesAsString) {
        this.categoriesAsString = categoriesAsString;
    }

    private void updateCategoriesAsString() {
        this.categoriesAsString = getCategoriesAsString();
    }

    // Category management methods
    public boolean hasCategory(Category category) {
        if (category == null) return false;
        return categories.stream()
            .anyMatch(c -> c != null && c.getId() == category.getId());
    }

    public void addCategory(Category category) {
        if (category == null) return;
        if (!hasCategory(category)) {
            categories.add(category);
            updateCategoriesAsString();
        }
    }

    public void removeCategory(Category category) {
        if (category == null) return;
        categories.removeIf(c -> c != null && c.getId() == category.getId());
        updateCategoriesAsString();
    }

    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id != null && id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return "Book{" +
            "id='" + id + '\'' +
            ", isbn='" + isbn + '\'' +
            ", title='" + title + '\'' +
            ", authors=" + getAuthorsAsString() +
            ", publisher=" + (publisher != null ? publisher.getName() : "null") +
            ", categories=" + getCategoriesAsString() +
            '}';
    }
}
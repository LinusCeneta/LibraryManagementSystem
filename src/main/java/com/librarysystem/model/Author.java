package com.librarysystem.model;

public class Author {
    private int authorID;
    private String authorName;
    private String biography; // Optional

    public Author() {
    }

    public Author(String authorName) {
        this.authorName = authorName;
    }

    // Getters and Setters
    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        return "Author{" +
                "authorID=" + authorID +
                ", authorName='" + authorName + '\'' +
                '}';
    }

    //hashCode and equals for use in Sets or as Map keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return authorID == author.authorID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(authorID);
    }
}

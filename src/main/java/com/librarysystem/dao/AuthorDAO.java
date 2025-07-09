package com.librarysystem.dao;

import com.librarysystem.model.Author;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorDAO {

    public Author createAuthor(Author author) throws SQLException {
        String sql = "INSERT INTO Authors (AuthorName, Biography) VALUES (?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, author.getAuthorName());
            pstmt.setString(2, author.getBiography()); // Can be null
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    author.setAuthorID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating author failed, no ID obtained.");
                }
            }
            return author;
        }
    }

    public Optional<Author> getAuthorById(int authorId) throws SQLException {
        String sql = "SELECT * FROM Authors WHERE AuthorID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAuthor(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Author> getAuthorByName(String name) throws SQLException {
        String sql = "SELECT * FROM Authors WHERE AuthorName = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAuthor(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Author> getAllAuthors() throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM Authors ORDER BY AuthorName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                authors.add(mapRowToAuthor(rs));
            }
        }
        return authors;
    }

    public List<Author> findAuthorsByName(String partialName) throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM Authors WHERE LOWER(AuthorName) LIKE ? ORDER BY AuthorName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + partialName.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    authors.add(mapRowToAuthor(rs));
                }
            }
        }
        return authors;
    }


    public boolean updateAuthor(Author author) throws SQLException {
        String sql = "UPDATE Authors SET AuthorName = ?, Biography = ? WHERE AuthorID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, author.getAuthorName());
            pstmt.setString(2, author.getBiography());
            pstmt.setInt(3, author.getAuthorID());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteAuthor(int authorId) throws SQLException {
        // Consider checking BookAuthors linking table for dependencies before deletion
        // Or handle with ON DELETE CASCADE/RESTRICT in DB schema
        String sql = "DELETE FROM Authors WHERE AuthorID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Author> getAuthorsForBook(int bookId) throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT a.* FROM Authors a JOIN BookAuthors ba ON a.AuthorID = ba.AuthorID WHERE ba.BookID = ? ORDER BY a.AuthorName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    authors.add(mapRowToAuthor(rs));
                }
            }
        }
        return authors;
    }


    private Author mapRowToAuthor(ResultSet rs) throws SQLException {
        Author author = new Author();
        author.setAuthorID(rs.getInt("AuthorID"));
        author.setAuthorName(rs.getString("AuthorName"));
        author.setBiography(rs.getString("Biography"));
        return author;
    }
}

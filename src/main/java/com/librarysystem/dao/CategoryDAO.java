package com.librarysystem.dao;

import com.librarysystem.model.Category;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAO {

    public Category createCategory(Category category) throws SQLException {
        String sql = "INSERT INTO Categories (CategoryName, ParentCategoryID, Description) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, category.getCategoryName());
            if (category.getParentCategoryID() != null) {
                pstmt.setInt(2, category.getParentCategoryID());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, category.getDescription()); // Can be null
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setCategoryID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating category failed, no ID obtained.");
                }
            }
            return category;
        }
    }

    public Optional<Category> getCategoryById(int categoryId) throws SQLException {
        String sql = "SELECT * FROM Categories WHERE CategoryID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCategory(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Category> getCategoryByName(String name) throws SQLException {
        String sql = "SELECT * FROM Categories WHERE CategoryName = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCategory(rs));
                }
            }
        }
        return Optional.empty();
    }


    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        // Could implement hierarchical fetching here if needed, or in a service layer
        String sql = "SELECT * FROM Categories ORDER BY ParentCategoryID NULLS FIRST, CategoryName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categories.add(mapRowToCategory(rs));
            }
        }
        return categories;
    }

    public List<Category> getTopLevelCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE ParentCategoryID IS NULL ORDER BY CategoryName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categories.add(mapRowToCategory(rs));
            }
        }
        return categories;
    }

    public List<Category> getChildCategories(int parentId) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE ParentCategoryID = ? ORDER BY CategoryName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, parentId);
            try(ResultSet rs = pstmt.executeQuery()){
                 while (rs.next()) {
                    categories.add(mapRowToCategory(rs));
                }
            }
        }
        return categories;
    }


    public boolean updateCategory(Category category) throws SQLException {
        String sql = "UPDATE Categories SET CategoryName = ?, ParentCategoryID = ?, Description = ? WHERE CategoryID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getCategoryName());
            if (category.getParentCategoryID() != null) {
                pstmt.setInt(2, category.getParentCategoryID());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, category.getDescription());
            pstmt.setInt(4, category.getCategoryID());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCategory(int categoryId) throws SQLException {
        // Consider impact on child categories (set null, cascade, or restrict)
        // Also check BookCategories linking table
        String sql = "DELETE FROM Categories WHERE CategoryID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Category> getCategoriesForBook(int bookId) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.* FROM Categories c JOIN BookCategories bc ON c.CategoryID = bc.CategoryID WHERE bc.BookID = ? ORDER BY c.CategoryName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapRowToCategory(rs));
                }
            }
        }
        return categories;
    }


    private Category mapRowToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryID(rs.getInt("CategoryID"));
        category.setCategoryName(rs.getString("CategoryName"));
        category.setParentCategoryID(rs.getObject("ParentCategoryID", Integer.class)); // Handles NULL
        category.setDescription(rs.getString("Description"));
        return category;
    }
}

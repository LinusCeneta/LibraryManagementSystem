package dao;

import model.Author;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {

    public List<Author> getAll() {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT id, name FROM Author";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                authors.add(new Author(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    public Author findById(int id) {
        String sql = "SELECT id, name FROM Author WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Author(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(Author author) {
        String sql = "INSERT INTO Author (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, author.getName());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) return false;

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                author.setId(generatedKeys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Author author) {
        String sql = "UPDATE Author SET name = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, author.getName());
            ps.setInt(2, author.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Author WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Author findByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT id, name FROM Author WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Author(rs.getInt("id"), rs.getString("name"));
            }
        }
        return null;
    }

    private Author createAuthor(Connection conn, String name) throws SQLException {
        String sql = "INSERT INTO Author (name) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return new Author(keys.getInt(1), name);
            }
        }
        return null;
    }


    public List<Author> findOrCreateList(String authorNames, Connection conn) throws SQLException {
        List<Author> authorList = new ArrayList<>();

        if (authorNames == null || authorNames.trim().isEmpty()) {
            return authorList;
        }

        String[] names = authorNames.split(",");
        for (String name : names) {
            String trimmedName = name.trim();
            Author author = findByName(conn, trimmedName);
            if (author == null) {
                author = createAuthor(conn, trimmedName);
            }
            if (author != null) {
                authorList.add(author);
            }
        }

        return authorList;
    }

}

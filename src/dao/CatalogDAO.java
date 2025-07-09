package dao;

import model.Item;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogDAO {
    private Connection connection;

    public CatalogDAO() throws SQLException {
        connection = DBConnection.getConnection();
    }

    public List<Item> searchCatalog(String keyword, String format, String genre, String language, String availability) throws SQLException {
        List<Item> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT i.item_id, i.title, i.author, i.genre, i.format, i.language, " +
                "SUM(CASE WHEN c.status = 'Available' THEN 1 ELSE 0 END) AS available_copies " +
                "FROM Item i " +
                "LEFT JOIN Copy c ON i.item_id = c.item_id " +
                "WHERE 1=1 "
        );

        // Dynamic filters
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.title LIKE ? OR i.author LIKE ? OR i.subject LIKE ?) ");
        }
        if (format != null && !format.trim().isEmpty()) {
            sql.append("AND i.format = ? ");
        }
        if (genre != null && !genre.trim().isEmpty()) {
            sql.append("AND i.genre = ? ");
        }
        if (language != null && !language.trim().isEmpty()) {
            sql.append("AND i.language = ? ");
        }
        if (availability != null && !availability.trim().isEmpty() && availability.equals("Available")) {
            sql.append("HAVING available_copies > 0 ");
        }

        sql.append("GROUP BY i.item_id, i.title, i.author, i.genre, i.format, i.language");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
                ps.setString(paramIndex++, "%" + keyword + "%");
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            if (format != null && !format.trim().isEmpty()) {
                ps.setString(paramIndex++, format);
            }
            if (genre != null && !genre.trim().isEmpty()) {
                ps.setString(paramIndex++, genre);
            }
            if (language != null && !language.trim().isEmpty()) {
                ps.setString(paramIndex++, language);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getInt("item_id"));
                item.setTitle(rs.getString("title"));
                item.setAuthor(rs.getString("author"));
                item.setGenre(rs.getString("genre"));
                item.setFormat(rs.getString("format"));
                item.setLanguage(rs.getString("language"));
                item.setAvailableCopies(rs.getInt("available_copies"));
                items.add(item);
            }
        }

        return items;
    }
}

package com.oceanview.dao;

import com.oceanview.model.Guest;
import com.oceanview.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuestDAO {

    public int insertGuest(Guest guest, Connection conn) throws SQLException {
        String query = "INSERT INTO guests (full_name, address, contact_number, email) VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, guest.getFullName());
            stmt.setString(2, guest.getAddress());
            stmt.setString(3, guest.getContactNumber());
            stmt.setString(4, guest.getEmail());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    guest.setId(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Creating guest failed, no ID obtained.");
                }
            }
        }
    }

    public void updateGuest(Guest guest, Connection conn) throws SQLException {
        String query = "UPDATE guests SET full_name = ?, address = ?, contact_number = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, guest.getFullName());
            stmt.setString(2, guest.getAddress());
            stmt.setString(3, guest.getContactNumber());
            stmt.setString(4, guest.getEmail());
            stmt.setInt(5, guest.getId());
            stmt.executeUpdate();
        }
    }
    
    public Guest getGuestById(int id) {
        String query = "SELECT id, full_name, address, contact_number, email, created_at FROM guests WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Guest guest = new Guest();
                    guest.setId(rs.getInt("id"));
                    guest.setFullName(rs.getString("full_name"));
                    guest.setAddress(rs.getString("address"));
                    guest.setContactNumber(rs.getString("contact_number"));
                    guest.setEmail(rs.getString("email"));
                    guest.setCreatedAt(rs.getTimestamp("created_at"));
                    return guest;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.oceanview.dao;

import com.oceanview.model.Room;
import com.oceanview.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> getAvailableRooms(Date checkIn, Date checkOut) {
        String query = "SELECT room_number, room_type, capacity, nightly_rate FROM rooms r " +
                       "WHERE NOT EXISTS (" +
                       "  SELECT 1 FROM reservations res " +
                       "  WHERE res.room_number = r.room_number " +
                       "  AND res.status = 'Active' " +
                       "  AND res.check_in_date < ? " +
                       "  AND res.check_out_date > ?" +
                       ") ORDER BY room_number ASC";
        List<Room> availableRooms = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDate(1, checkOut);
            stmt.setDate(2, checkIn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomType(rs.getString("room_type"));
                    room.setCapacity(rs.getInt("capacity"));
                    room.setNightlyRate(rs.getBigDecimal("nightly_rate"));
                    availableRooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableRooms;
    }

    public Room getRoomByNumber(String roomNumber) {
        String query = "SELECT room_number, room_type, capacity, nightly_rate FROM rooms WHERE room_number = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, roomNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomType(rs.getString("room_type"));
                    room.setCapacity(rs.getInt("capacity"));
                    room.setNightlyRate(rs.getBigDecimal("nightly_rate"));
                    return room;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

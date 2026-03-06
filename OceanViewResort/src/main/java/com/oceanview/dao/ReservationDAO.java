package com.oceanview.dao;

import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public synchronized String generateNextReservationNumber(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM reservations";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                return String.format("RES-%05d", count + 1);
            }
        }
        return "RES-00001";
    }

    public boolean isRoomAvailable(String roomNumber, java.sql.Date checkIn, java.sql.Date checkOut, String ignoreReservationId) {
        String query = "SELECT 1 FROM reservations WHERE room_number = ? AND status = 'Active' " +
                       "AND check_in_date < ? AND check_out_date > ?";
        if (ignoreReservationId != null) {
            query += " AND reservation_number != ?";
        }
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, roomNumber);
            stmt.setDate(2, checkOut);
            stmt.setDate(3, checkIn);
            if (ignoreReservationId != null) {
                stmt.setString(4, ignoreReservationId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next(); // True if available
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createReservationWithGuest(Reservation reservation, Guest guest) {
        GuestDAO guestDAO = new GuestDAO();

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                int guestId = guestDAO.insertGuest(guest, conn);
                String resNumber = generateNextReservationNumber(conn);
                reservation.setReservationNumber(resNumber);
                reservation.setGuestId(guestId);

                String insertRes = "INSERT INTO reservations (reservation_number, guest_id, room_number, check_in_date, check_out_date, number_of_guests, special_requests, status) " +
                                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                                   
                try (PreparedStatement stmt = conn.prepareStatement(insertRes)) {
                    stmt.setString(1, reservation.getReservationNumber());
                    stmt.setInt(2, reservation.getGuestId());
                    stmt.setString(3, reservation.getRoomNumber());
                    stmt.setDate(4, reservation.getCheckInDate());
                    stmt.setDate(5, reservation.getCheckOutDate());
                    stmt.setInt(6, reservation.getNumberOfGuests());
                    stmt.setString(7, reservation.getSpecialRequests());
                    stmt.setString(8, "Active");
                    
                    stmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateReservationAndGuest(Reservation reservation, Guest guest) {
        GuestDAO guestDAO = new GuestDAO();
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                guestDAO.updateGuest(guest, conn);
                
                String updateRes = "UPDATE reservations SET room_number = ?, check_in_date = ?, check_out_date = ?, number_of_guests = ?, special_requests = ?, updated_at = CURRENT_TIMESTAMP WHERE reservation_number = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateRes)) {
                    stmt.setString(1, reservation.getRoomNumber());
                    stmt.setDate(2, reservation.getCheckInDate());
                    stmt.setDate(3, reservation.getCheckOutDate());
                    stmt.setInt(4, reservation.getNumberOfGuests());
                    stmt.setString(5, reservation.getSpecialRequests());
                    stmt.setString(6, reservation.getReservationNumber());
                    
                    stmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cancelReservation(String reservationNumber, String reason) {
        String query = "UPDATE reservations SET status = 'Cancelled', cancellation_reason = ?, updated_at = CURRENT_TIMESTAMP WHERE reservation_number = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, reason);
            stmt.setString(2, reservationNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Reservation getReservation(String reservationNumber) {
        String query = "SELECT r.*, g.id as g_id, g.full_name, g.address, g.contact_number, g.email, g.created_at as g_created, rm.room_type, rm.capacity, rm.nightly_rate " +
                       "FROM reservations r " +
                       "JOIN guests g ON r.guest_id = g.id " +
                       "JOIN rooms rm ON r.room_number = rm.room_number " +
                       "WHERE r.reservation_number = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, reservationNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Reservation> getAllReservations(String statusFilter, String searchQuery, int offset, int limit) {
        List<Reservation> list = new ArrayList<>();
        
        StringBuilder queryBuilder = new StringBuilder("SELECT r.*, g.id as g_id, g.full_name, g.address, g.contact_number, g.email, g.created_at as g_created, rm.room_type, rm.capacity, rm.nightly_rate " +
                       "FROM reservations r " +
                       "JOIN guests g ON r.guest_id = g.id " +
                       "JOIN rooms rm ON r.room_number = rm.room_number WHERE 1=1 ");
                       
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !statusFilter.equals("All")) {
            queryBuilder.append(" AND r.status = ? ");
        }
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            queryBuilder.append(" AND (r.reservation_number ILIKE ? OR g.full_name ILIKE ?) ");
        }
        
        queryBuilder.append(" ORDER BY r.created_at DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            
            int paramIndex = 1;
            
            if (statusFilter != null && !statusFilter.trim().isEmpty() && !statusFilter.equals("All")) {
                stmt.setString(paramIndex++, statusFilter);
            }
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String likeSearch = "%" + searchQuery + "%";
                stmt.setString(paramIndex++, likeSearch);
                stmt.setString(paramIndex++, likeSearch);
            }
            
            stmt.setInt(paramIndex++, limit);
            stmt.setInt(paramIndex, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public int getTotalReservationsCount(String statusFilter, String searchQuery) {
        StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(*) FROM reservations r JOIN guests g ON r.guest_id = g.id WHERE 1=1 ");
                       
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !statusFilter.equals("All")) {
            queryBuilder.append(" AND r.status = ? ");
        }
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            queryBuilder.append(" AND (r.reservation_number ILIKE ? OR g.full_name ILIKE ?) ");
        }

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            
            int paramIndex = 1;
            
            if (statusFilter != null && !statusFilter.trim().isEmpty() && !statusFilter.equals("All")) {
                stmt.setString(paramIndex++, statusFilter);
            }
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String likeSearch = "%" + searchQuery + "%";
                stmt.setString(paramIndex++, likeSearch);
                stmt.setString(paramIndex++, likeSearch);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Dashboard queries
    public int getTodayTotalReservations() {
        String query = "SELECT COUNT(*) FROM reservations WHERE DATE(created_at) = CURRENT_DATE";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return 0;
    }
    
    public int getCurrentlyOccupiedRoomsCount() {
        String query = "SELECT COUNT(*) FROM reservations WHERE status = 'Active' AND check_in_date <= CURRENT_DATE AND check_out_date >= CURRENT_DATE";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return 0;
    }
    
    public int getUpcomingCheckIns7Days() {
        String query = "SELECT COUNT(*) FROM reservations WHERE status = 'Active' AND check_in_date > CURRENT_DATE AND check_in_date <= CURRENT_DATE + INTERVAL '7 days'";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return 0;
    }
    
    public List<Reservation> getRecentReservations(int limit) {
        String query = "SELECT r.*, g.id as g_id, g.full_name, g.address, g.contact_number, g.email, g.created_at as g_created, rm.room_type, rm.capacity, rm.nightly_rate " +
                       "FROM reservations r " +
                       "JOIN guests g ON r.guest_id = g.id " +
                       "JOIN rooms rm ON r.room_number = rm.room_number " +
                       "ORDER BY r.created_at DESC LIMIT ?";
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation res = new Reservation();
        res.setReservationNumber(rs.getString("reservation_number"));
        res.setGuestId(rs.getInt("guest_id"));
        res.setRoomNumber(rs.getString("room_number"));
        res.setCheckInDate(rs.getDate("check_in_date"));
        res.setCheckOutDate(rs.getDate("check_out_date"));
        res.setNumberOfGuests(rs.getInt("number_of_guests"));
        res.setSpecialRequests(rs.getString("special_requests"));
        res.setStatus(rs.getString("status"));
        res.setCancellationReason(rs.getString("cancellation_reason"));
        res.setCreatedAt(rs.getTimestamp("created_at"));
        res.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        Guest guest = new Guest();
        guest.setId(rs.getInt("g_id"));
        guest.setFullName(rs.getString("full_name"));
        guest.setAddress(rs.getString("address"));
        guest.setContactNumber(rs.getString("contact_number"));
        guest.setEmail(rs.getString("email"));
        guest.setCreatedAt(rs.getTimestamp("g_created"));
        res.setGuest(guest);
        
        Room room = new Room();
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setCapacity(rs.getInt("capacity"));
        room.setNightlyRate(rs.getBigDecimal("nightly_rate"));
        res.setRoom(room);
        
        return res;
    }
}

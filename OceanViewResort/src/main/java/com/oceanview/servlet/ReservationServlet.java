package com.oceanview.servlet;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.util.JsonUtil;
import com.oceanview.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/api/reservations/*")
public class ReservationServlet extends HttpServlet {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    static class ReservationPayload {
        String fullName;
        String address;
        String contactNumber;
        String email;
        String roomNumber;
        String checkInDate;
        String checkOutDate;
        int numberOfGuests;
        String specialRequests;
        String cancellationReason;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/reservations
            String statusFilter = req.getParameter("status");
            String searchQuery = req.getParameter("search");
            String pageParam = req.getParameter("page");
            
            int page = (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;
            int limit = 10;
            int offset = (page - 1) * limit;

            List<Reservation> list = reservationDAO.getAllReservations(statusFilter, searchQuery, offset, limit);
            int total = reservationDAO.getTotalReservationsCount(statusFilter, searchQuery);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", list);
            response.put("total", total);
            response.put("page", page);
            response.put("limit", limit);

            resp.getWriter().write(JsonUtil.toJson(response));
        } else {
            // GET /api/reservations/{id}
            String reservationNumber = pathInfo.substring(1);
            Reservation res = reservationDAO.getReservation(reservationNumber);
            if (res != null) {
                resp.getWriter().write(JsonUtil.toJson(res));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Reservation not found.\"}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String jsonBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ReservationPayload payload = JsonUtil.fromJson(jsonBody, ReservationPayload.class);

            if (!validatePayload(payload, resp)) return;

            Room room = roomDAO.getRoomByNumber(payload.roomNumber);
            if (room == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid room.");
                return;
            }
            if (payload.numberOfGuests > room.getCapacity()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Guests exceed capacity limit of " + room.getCapacity() + ".");
                return;
            }

            Date checkIn = Date.valueOf(payload.checkInDate);
            Date checkOut = Date.valueOf(payload.checkOutDate);

            if (!reservationDAO.isRoomAvailable(payload.roomNumber, checkIn, checkOut, null)) {
                sendError(resp, HttpServletResponse.SC_CONFLICT, "Room is not available for selected dates.");
                return;
            }

            Guest guest = new Guest();
            guest.setFullName(payload.fullName);
            guest.setAddress(payload.address);
            guest.setContactNumber(payload.contactNumber);
            guest.setEmail(payload.email);

            Reservation res = new Reservation();
            res.setRoomNumber(payload.roomNumber);
            res.setCheckInDate(checkIn);
            res.setCheckOutDate(checkOut);
            res.setNumberOfGuests(payload.numberOfGuests);
            res.setSpecialRequests(payload.specialRequests);

            if (reservationDAO.createReservationWithGuest(res, guest)) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("{\"message\":\"Reservation created successfully!\", \"reservationNumber\":\"" + res.getReservationNumber() + "\"}");
            } else {
                sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create reservation due to server error.");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String reservationNumber = parts[1];
        String jsonBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        ReservationPayload payload = JsonUtil.fromJson(jsonBody, ReservationPayload.class);

        if (parts.length == 3 && "cancel".equals(parts[2])) {
            if (payload == null || ValidationUtil.isNullOrEmpty(payload.cancellationReason)) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Cancellation reason is required.");
                return;
            }
            if (reservationDAO.cancelReservation(reservationNumber, payload.cancellationReason)) {
                resp.getWriter().write("{\"message\":\"Reservation successfully cancelled.\"}");
            } else {
                sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to cancel or reservation not found.");
            }
        } else if (parts.length == 2) {
            // Update
            Reservation existing = reservationDAO.getReservation(reservationNumber);
            if (existing == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Reservation not found.");
                return;
            }
            
            if (!validatePayload(payload, resp)) return;
            
            Room room = roomDAO.getRoomByNumber(payload.roomNumber);
            if (room == null || payload.numberOfGuests > room.getCapacity()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid room or guests exceed capacity limit.");
                return;
            }

            Date checkIn = Date.valueOf(payload.checkInDate);
            Date checkOut = Date.valueOf(payload.checkOutDate);

            // Checking availability while ignoring the current res ID
            if (!reservationDAO.isRoomAvailable(payload.roomNumber, checkIn, checkOut, reservationNumber)) {
                sendError(resp, HttpServletResponse.SC_CONFLICT, "Room is not available for new selected dates.");
                return;
            }

            Guest guest = existing.getGuest();
            guest.setFullName(payload.fullName);
            guest.setAddress(payload.address);
            guest.setContactNumber(payload.contactNumber);
            guest.setEmail(payload.email);

            existing.setRoomNumber(payload.roomNumber);
            existing.setCheckInDate(checkIn);
            existing.setCheckOutDate(checkOut);
            existing.setNumberOfGuests(payload.numberOfGuests);
            existing.setSpecialRequests(payload.specialRequests);

            if (reservationDAO.updateReservationAndGuest(existing, guest)) {
                resp.getWriter().write("{\"message\":\"Reservation updated!\"}");
            } else {
                sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update reservation.");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private boolean validatePayload(ReservationPayload payload, HttpServletResponse resp) throws IOException {
        if (payload == null) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Payload required.");
            return false;
        }
        if (!ValidationUtil.isValidName(payload.fullName)) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Guest full name is invalid (minimum 2 letters).");
            return false;
        }
        if (!ValidationUtil.isValidPhoneNumber(payload.contactNumber)) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Contact Number must be precisely 10 numerical digits.");
            return false;
        }
        if (!ValidationUtil.isValidEmail(payload.email)) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Email format is invalid.");
            return false;
        }
        if (ValidationUtil.isNullOrEmpty(payload.checkInDate) || ValidationUtil.isNullOrEmpty(payload.checkOutDate)) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Check-in and Check-out dates are strictly required.");
            return false;
        }
        try {
            Date checkIn = Date.valueOf(payload.checkInDate);
            Date checkOut = Date.valueOf(payload.checkOutDate);
            if (!checkOut.after(checkIn)) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Check-out must be after check-in.");
                return false;
            }
            if (checkIn.before(new Date(System.currentTimeMillis() - 86400000L))) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Check-in cannot be set to a past date.");
                return false;
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid date format, use YYYY-MM-DD.");
            return false;
        }
        if (payload.numberOfGuests < 1) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Guests count must be at least 1.");
            return false;
        }
        if (ValidationUtil.isNullOrEmpty(payload.roomNumber)) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Room number must be selected.");
            return false;
        }
        return true;
    }

    private void sendError(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write("{\"error\":\"" + msg + "\"}");
    }
}

package com.oceanview.servlet;

import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Room;
import com.oceanview.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/api/rooms/available")
public class RoomServlet extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        
        String checkInParam = req.getParameter("checkIn");
        String checkOutParam = req.getParameter("checkOut");

        if (checkInParam == null || checkOutParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"checkIn and checkOut dates are required.\"}");
            return;
        }

        try {
            Date checkIn = Date.valueOf(checkInParam);
            Date checkOut = Date.valueOf(checkOutParam);

            if (!checkOut.after(checkIn)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Check-out date must be after check-in date.\"}");
                return;
            }

            List<Room> rooms = roomDAO.getAvailableRooms(checkIn, checkOut);
            resp.getWriter().write(JsonUtil.toJson(rooms));

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid date format. Use YYYY-MM-DD.\"}");
        }
    }
}

package com.oceanview.servlet;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/dashboard")
public class DashboardServlet extends HttpServlet {

    private final ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        Map<String, Object> data = new HashMap<>();
        data.put("totalReservationsToday", reservationDAO.getTodayTotalReservations());
        data.put("occupiedRoomsCount", reservationDAO.getCurrentlyOccupiedRoomsCount());
        data.put("upcomingCheckIns", reservationDAO.getUpcomingCheckIns7Days());
        data.put("recentReservations", reservationDAO.getRecentReservations(5));

        resp.getWriter().write(JsonUtil.toJson(data));
    }
}

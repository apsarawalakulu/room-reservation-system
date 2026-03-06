package com.oceanview.servlet;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.Reservation;
import com.oceanview.util.BillCalculator;
import com.oceanview.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/bill")
public class BillServlet extends HttpServlet {

    private final ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String resId = req.getParameter("reservationId");
        if (resId == null || resId.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Reservation ID is required via query param.\"}");
            return;
        }

        Reservation res = reservationDAO.getReservation(resId);
        if (res == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Reservation not found.\"}");
            return;
        }

        BillCalculator.BillDetails details = BillCalculator.calculate(res);

        Map<String, Object> result = new HashMap<>();
        result.put("reservationNumber", res.getReservationNumber());
        result.put("guestName", res.getGuest().getFullName());
        result.put("guestAddress", res.getGuest().getAddress());
        result.put("roomType", res.getRoom().getRoomType());
        result.put("roomNumber", res.getRoom().getRoomNumber());
        result.put("checkIn", res.getCheckInDate().toString());
        result.put("checkOut", res.getCheckOutDate().toString());
        result.put("numberOfNights", details.numberOfNights);
        result.put("nightlyRate", details.nightlyRate);
        result.put("subTotal", details.subTotal);
        result.put("tax", details.tax);
        result.put("grandTotal", details.grandTotal);
        result.put("generatedDate", new java.sql.Date(System.currentTimeMillis()).toString());

        resp.getWriter().write(JsonUtil.toJson(result));
    }
}

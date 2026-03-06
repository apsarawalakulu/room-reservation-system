package com.oceanview.servlet;

import com.oceanview.dao.GuestDAO;
import com.oceanview.model.Guest;
import com.oceanview.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/guests/*")
public class GuestServlet extends HttpServlet {

    private final GuestDAO guestDAO = new GuestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                Guest guest = guestDAO.getGuestById(id);
                if (guest != null) {
                    resp.getWriter().write(JsonUtil.toJson(guest));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Guest not found.\"}");
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid Guest ID format.\"}");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            resp.getWriter().write("{\"error\":\"General list not required / implemented.\"}");
        }
    }
}

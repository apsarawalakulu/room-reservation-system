package com.oceanview.servlet;

import com.oceanview.dao.UserDAO;
import com.oceanview.model.User;
import com.oceanview.util.JsonUtil;
import com.oceanview.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    static class LoginRequest {
        String username;
        String password;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if ("/login".equals(pathInfo)) {
            String jsonBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            LoginRequest loginReq = JsonUtil.fromJson(jsonBody, LoginRequest.class);
            
            if (loginReq == null || loginReq.username == null || loginReq.password == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Username and password required.\"}");
                return;
            }

            User user = userDAO.getUserByUsername(loginReq.username);
            if (user != null && user.getPasswordHash().equals(PasswordUtil.hashPassword(loginReq.password))) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                resp.getWriter().write("{\"message\":\"Login successful.\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\":\"Invalid credentials.\"}");
            }

        } else if ("/logout".equals(pathInfo)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.getWriter().write("{\"message\":\"Logged out.\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Not found.\"}");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        
        if ("/status".equals(pathInfo)) {
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                resp.getWriter().write("{\"authenticated\":true, \"username\":\"" + user.getUsername() + "\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"authenticated\":false}");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

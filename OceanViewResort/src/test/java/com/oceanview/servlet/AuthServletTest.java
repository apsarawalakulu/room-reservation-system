package com.oceanview.servlet;

import com.oceanview.dao.UserDAO;
import com.oceanview.model.User;
import com.oceanview.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServletTest {

    private AuthServlet authServlet;

    @Mock
    private UserDAO mockUserDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        authServlet = new AuthServlet();
        
        // Inject mock via reflection for unit isolation
        Field userDAOField = AuthServlet.class.getDeclaredField("userDAO");
        userDAOField.setAccessible(true);
        userDAOField.set(authServlet, mockUserDAO);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testValidLogin() throws Exception {
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\":\"admin\",\"password\":\"admin123\"}")));
        
        User mockUser = new User();
        mockUser.setUsername("admin");
        mockUser.setPasswordHash(PasswordUtil.hashPassword("admin123"));
        
        when(mockUserDAO.getUserByUsername("admin")).thenReturn(mockUser);
        when(request.getSession(true)).thenReturn(session);

        authServlet.doPost(request, response);

        verify(session, times(1)).setAttribute("user", mockUser);
        assertTrue(responseWriter.toString().contains("Login successful"));
    }

    @Test
    void testInvalidLogin() throws Exception {
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\":\"admin\",\"password\":\"wrongpass\"}")));
        
        User mockUser = new User();
        mockUser.setUsername("admin");
        mockUser.setPasswordHash(PasswordUtil.hashPassword("admin123"));
        
        when(mockUserDAO.getUserByUsername("admin")).thenReturn(mockUser);

        authServlet.doPost(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseWriter.toString().contains("Invalid credentials"));
    }
}

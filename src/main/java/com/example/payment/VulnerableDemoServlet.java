package com.example.payment;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * DELIBERATELY VULNERABLE - demo class for testing CodeQL detection.
 * Servlet request parameters are recognized "taint sources" by CodeQL,
 * flowing into unsafe sinks (SQL, OS command, file read).
 */
public class VulnerableDemoServlet extends HttpServlet {

    public ResultSet queryUser(Connection conn, HttpServletRequest req) throws Exception {
        // VULNERABILITY 1: SQL Injection
        String userId = req.getParameter("userId");
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM users WHERE id = '" + userId + "'";
        return stmt.executeQuery(sql);
    }

    public void runCommand(HttpServletRequest req) throws Exception {
        // VULNERABILITY 2: OS Command Injection
        String input = req.getParameter("host");
        String cmd = "ping " + input;
        Runtime.getRuntime().exec(cmd);
    }

    public String readFile(HttpServletRequest req) throws Exception {
        // VULNERABILITY 3: Path Traversal
        String fileName = req.getParameter("file");
        java.nio.file.Path path = java.nio.file.Paths.get("/data/uploads/" + fileName);
        return new String(java.nio.file.Files.readAllBytes(path));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws javax.servlet.ServletException, java.io.IOException {
        try {
            queryUser(null, req);
            runCommand(req);
            readFile(req);
        } catch (Exception e) {
            throw new javax.servlet.ServletException(e);
        }
    }
}

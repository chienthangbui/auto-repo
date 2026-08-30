package com.example.payment;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * SECURED version of the demo - shows how to fix the three vulnerabilities
 * that CodeQL detected in the previous version of this class.
 */
public class VulnerableDemoServlet extends HttpServlet {

    public ResultSet queryUser(Connection conn, HttpServletRequest req) throws SQLException {
        // FIX 1: SQL Injection -> use parameterized PreparedStatement
        if (conn == null) {
            throw new IllegalArgumentException("Database connection is required");
        }
        String userId = req.getParameter("userId");
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, username, email FROM users WHERE id = ?");
        stmt.setString(1, userId);
        return stmt.executeQuery();
    }

    // FIX 2: OS Command Injection -> user input only selects a fixed, known-safe host
    private static final Map<String, String> ALLOWED_HOSTS = Map.of(
            "local", "localhost",
            "loopback", "127.0.0.1",
            "example", "example.com",
            "test", "test-host");

    public void runCommand(HttpServletRequest req) throws IOException {
        String key = req.getParameter("host");
        // The raw user input is never passed to the command - it only picks a fixed value.
        String host = ALLOWED_HOSTS.get(key);
        if (host == null) {
            throw new IllegalArgumentException("Host not allowed");
        }
        ProcessBuilder pb = new ProcessBuilder("ping", "-c", "1", host);
        pb.start();
    }

    public String readFile(HttpServletRequest req) throws IOException {
        // FIX 3: Path Traversal -> validate and normalize the path
        String fileName = req.getParameter("file");
        Path baseDir = Paths.get("/data/uploads").toAbsolutePath().normalize();
        Path path = baseDir.resolve(fileName).normalize();
        if (!path.startsWith(baseDir)) {
            throw new IOException("Invalid file path: traversal attempt rejected");
        }
        return new String(Files.readAllBytes(path));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws javax.servlet.ServletException, IOException {
        try {
            queryUser(null, req);
            runCommand(req);
            readFile(req);
        } catch (SQLException | IllegalArgumentException e) {
            throw new javax.servlet.ServletException(e);
        }
    }
}

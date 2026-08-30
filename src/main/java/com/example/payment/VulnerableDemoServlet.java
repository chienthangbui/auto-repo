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
import java.util.List;

/**
 * SECURED version of the demo - shows how to fix the three vulnerabilities
 * that CodeQL detected in the previous version of this class.
 */
public class VulnerableDemoServlet extends HttpServlet {

    public ResultSet queryUser(Connection conn, HttpServletRequest req) throws Exception {
        // FIX 1: SQL Injection -> use parameterized PreparedStatement
        String userId = req.getParameter("userId");
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM users WHERE id = ?");
        stmt.setString(1, userId);
        return stmt.executeQuery();
    }

    // FIX 2: OS Command Injection -> only allow known-safe hosts (allowlist)
    private static final List<String> ALLOWED_HOSTS = List.of(
            "localhost", "127.0.0.1", "example.com", "test-host");

    public void runCommand(HttpServletRequest req) throws Exception {
        String input = req.getParameter("host");
        // Allowlist: reject anything not explicitly permitted. Regex is not enough.
        if (!ALLOWED_HOSTS.contains(input)) {
            throw new IllegalArgumentException("Host not allowed");
        }
        ProcessBuilder pb = new ProcessBuilder("ping", "-c", "1", input);
        pb.start();
    }

    public String readFile(HttpServletRequest req) throws Exception {
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
        } catch (Exception e) {
            throw new javax.servlet.ServletException(e);
        }
    }
}

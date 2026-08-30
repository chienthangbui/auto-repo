package com.example.payment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * DELIBERATELY VULNERABLE - demo class for testing CodeQL detection.
 * Contains SQL injection, command injection and path traversal flaws.
 */
public class VulnerableDemo {

    public ResultSet queryUser(Connection conn, String userId) throws Exception {
        // VULNERABILITY 1: SQL Injection - user input concatenated into SQL
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM users WHERE id = '" + userId + "'";
        return stmt.executeQuery(sql);
    }

    public void runCommand(String input) throws Exception {
        // VULNERABILITY 2: OS Command Injection
        String cmd = "ping " + input;
        Runtime.getRuntime().exec(cmd);
    }

    public String readFile(String fileName) throws Exception {
        // VULNERABILITY 3: Path Traversal
        java.nio.file.Path path = java.nio.file.Paths.get("/data/uploads/" + fileName);
        return new String(java.nio.file.Files.readAllBytes(path));
    }
}

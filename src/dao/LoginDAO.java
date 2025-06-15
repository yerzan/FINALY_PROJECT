package dao;

import database.DatabaseConnection;

import java.sql.*;

public class LoginDAO {
    public boolean sprawdzLogin(String login, String haslo) {
        String sql = "SELECT * FROM uzytkownicy WHERE login = ? AND haslo = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, haslo);
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

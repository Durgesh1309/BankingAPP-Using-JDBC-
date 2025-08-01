package com.bank.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

import com.bank.model.User;

public class UserDatabase {
    private final Connection conn = DBConnection.getInstance().getConnection();

    public int createUser(User user) {
        String sql = "INSERT INTO users (name, mobile) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getMobile());
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Mobile number already registered.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public User findUserByMobile(String mobile) {
        String sql = "SELECT * FROM users WHERE mobile = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mobile);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("mobile"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("mobile"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserName(int userId, String newName) {
        String sql = "UPDATE users SET name = ? WHERE user_id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean updateUserMobile(int userId, String newMobile) {
        String checkSql = "SELECT user_id FROM users WHERE mobile = ?";
        String updateSql = "UPDATE users SET mobile = ? WHERE user_id = ?";
        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, newMobile);
            try (ResultSet rs = checkPs.executeQuery()) {
                if(rs.next()) {
                    
                    return false;
                }
            }
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setString(1, newMobile);
                updatePs.setInt(2, userId);
                return updatePs.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

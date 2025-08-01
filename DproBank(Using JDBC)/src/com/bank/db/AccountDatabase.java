package com.bank.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.bank.model.Account;

public class AccountDatabase {
    private final Connection conn = DBConnection.getInstance().getConnection();

    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts (account_id, user_id, upi_id, pin, balance, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, account.getAccountId());
            ps.setInt(2, account.getUserId());
            ps.setString(3, account.getUpiId());
            ps.setString(4, account.getPin());
            ps.setDouble(5, account.getBalance());
            ps.setInt(6, account.getStatus());
            return ps.executeUpdate() == 1;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("AccountID or UPI already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Account getAccountById(int accId) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToAccount(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Account> getAccountsByUserId(int userId) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateBalance(int accountId, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setInt(2, accountId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePin(int accountId, String newPin) {
        String sql = "UPDATE accounts SET pin = ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPin);
            ps.setInt(2, accountId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean closeAccount(int accountId) {
        String sql = "UPDATE accounts SET status = 0 WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAccountActive(int accountId){
        String sql = "SELECT status FROM accounts WHERE account_id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, accountId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt("status") == 1;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    
    public int generateUniqueAccountNumber(){
        String sql = "SELECT MAX(account_id) AS maxAcc FROM accounts";
        try(PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            if(rs.next()){
                int max = rs.getInt("maxAcc");
                return (max >= 10000000) ? max + 1 : 10000000;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return 10000000;
    }

    public String generateUniqueUpiId(String mobile, int suffix){
        if(suffix <= 1){
            return mobile + "@dprobank";
        }
        return mobile + "_" + suffix + "@dprobank";
    }

    public boolean upiIdExists(String upiId){
        String sql = "SELECT COUNT(*) AS cnt FROM accounts WHERE upi_id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, upiId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt("cnt") > 0;
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                list.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public Account findAccountByUpi(String upiId) {
        String sql = "SELECT * FROM accounts WHERE upi_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, upiId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        return new Account(
            rs.getInt("account_id"),
            rs.getInt("user_id"),
            rs.getString("upi_id"),
            rs.getString("pin"),
            rs.getDouble("balance"),
            rs.getInt("status")
        );
    }
}

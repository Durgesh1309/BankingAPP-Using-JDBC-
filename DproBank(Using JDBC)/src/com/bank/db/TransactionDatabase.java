package com.bank.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.bank.model.Transaction;

public class TransactionDatabase {
    private final Connection conn = DBConnection.getInstance().getConnection();

    public boolean insertTransaction(Transaction txn) {
        String sql = "INSERT INTO transactions (account_id, txn_type, amount, details, balance_after, txn_time) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, txn.getAccountId());
            ps.setString(2, txn.getTxnType());
            ps.setDouble(3, txn.getAmount());
            ps.setString(4, txn.getDetails());
            ps.setDouble(5, txn.getBalanceAfter());
            ps.setTimestamp(6, Timestamp.valueOf(txn.getTxnTime()));
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Transaction> getTransactionHistoryByAccount(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY txn_time DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getMiniStatement(int accountId, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY txn_time DESC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("txn_id"),
                rs.getInt("account_id"),
                rs.getString("txn_type"),
                rs.getDouble("amount"),
                rs.getString("details"),
                rs.getDouble("balance_after"),
                rs.getTimestamp("txn_time").toLocalDateTime()
        );
    }
}

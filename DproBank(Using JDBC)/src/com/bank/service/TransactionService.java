package com.bank.service;

import java.time.LocalDateTime;
import java.util.List;

import com.bank.db.TransactionDatabase;
import com.bank.model.Transaction;

public class TransactionService {
    private final TransactionDatabase txnDb = new TransactionDatabase();

    public boolean recordTransaction(int accountId, String txnType, double amount, String details, double balanceAfter) {
        Transaction txn = new Transaction();
        txn.setAccountId(accountId);
        txn.setTxnType(txnType);
        txn.setAmount(amount);
        txn.setDetails(details);
        txn.setBalanceAfter(balanceAfter);
        txn.setTxnTime(LocalDateTime.now());
        return txnDb.insertTransaction(txn);
    }

    public List<Transaction> getTransactionHistoryByAccount(int accountId) {
        return txnDb.getTransactionHistoryByAccount(accountId);
    }

    public List<Transaction> getMiniStatement(int accountId) {
        return txnDb.getMiniStatement(accountId, 5);
    }
}

package com.bank.service;

import java.util.List;

import com.bank.db.AccountDatabase;
import com.bank.model.Account;

public class AccountService {
    private final AccountDatabase accountDb = new AccountDatabase();

    public Account createAccount(int userId, String mobile, String pin, double initialBalance) {
        int accountNum = accountDb.generateUniqueAccountNumber();
        int suffix = 1;
        String upi;
        do {
            upi = accountDb.generateUniqueUpiId(mobile, suffix++);
        } while (accountDb.upiIdExists(upi));
        Account acc = new Account(accountNum, userId, upi, pin, initialBalance, 1);
        return accountDb.createAccount(acc) ? acc : null;
    }

    public Account getAccountById(int accountId) {
        return accountDb.getAccountById(accountId);
    }

    public List<Account> getUserAccounts(int userId) {
        return accountDb.getAccountsByUserId(userId);
    }

    public boolean updateBalance(int accountId, double newBalance) {
        return accountDb.updateBalance(accountId, newBalance);
    }

    public boolean updatePin(int accountId, String newPin) {
        return accountDb.updatePin(accountId, newPin);
    }

    public boolean closeAccount(int accountId) {
        return accountDb.closeAccount(accountId);
    }

    public boolean isAccountActive(int accountId) {
        return accountDb.isAccountActive(accountId);
    }
    public Account findAccountByUpi(String upiId) {
        if (upiId == null || upiId.isEmpty()) {
            return null;
        }
        return accountDb.findAccountByUpi(upiId);
    }

}

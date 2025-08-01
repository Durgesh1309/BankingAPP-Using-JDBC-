package com.bank.model;

import java.time.LocalDateTime;

public class Transaction {
    private int txnId;
    private int accountId;
    private String txnType;
    private double amount;
    private String details;
    private double balanceAfter;
    private LocalDateTime txnTime;

    public Transaction() {}

    public Transaction(int txnId, int accountId, String txnType, double amount, String details, double balanceAfter, LocalDateTime txnTime) {
        this.txnId = txnId;
        this.accountId = accountId;
        this.txnType = txnType;
        this.amount = amount;
        this.details = details;
        this.balanceAfter = balanceAfter;
        this.txnTime = txnTime;
    }

	public int getTxnId() {
		return txnId;
	}

	public void setTxnId(int txnId) {
		this.txnId = txnId;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public double getBalanceAfter() {
		return balanceAfter;
	}

	public void setBalanceAfter(double balanceAfter) {
		this.balanceAfter = balanceAfter;
	}

	public LocalDateTime getTxnTime() {
		return txnTime;
	}

	public void setTxnTime(LocalDateTime txnTime) {
		this.txnTime = txnTime;
	}

   
}

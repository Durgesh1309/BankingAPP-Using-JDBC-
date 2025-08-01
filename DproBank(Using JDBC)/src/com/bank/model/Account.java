package com.bank.model;


public class Account {
    private int accountId;
    private int userId;
    private String upiId;
    private String pin;
    private double balance;
    private int status;

    public Account() {}

    public Account(int accountId, int userId, String upiId, String pin, double balance, int status) {
        this.accountId = accountId;
        this.userId = userId;
        this.upiId = upiId;
        this.pin = pin;
        this.balance = balance;
        this.status = status;
    }

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUpiId() {
		return upiId;
	}

	public void setUpiId(String upiId) {
		this.upiId = upiId;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
    

   
}

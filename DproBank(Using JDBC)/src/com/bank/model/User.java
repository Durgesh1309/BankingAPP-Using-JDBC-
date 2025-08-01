package com.bank.model;



public class User {
    private int userId;
    private String name;
    private String mobile;

    public User() {}

    public User(int userId, String name, String mobile) {
        this.userId = userId;
        this.name = name;
        this.mobile = mobile;
    }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
    
}


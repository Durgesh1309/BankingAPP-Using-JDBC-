package com.bank.ui;

public class InputValidator {
    public static boolean isValidMobile(String mobile) {
        return mobile != null && mobile.matches("\\d{10}");
    }
    public static boolean isValidPin(String pin) {
        return pin != null && pin.matches("\\d{4}");
    }
    public static boolean isValidAmount(String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);
            return amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isValidName(String name) {
        return name != null && name.trim().matches("^[A-Za-z\\s\\-']+$");
    }
    public static boolean isValidAccountNumber(String accNo) {
        return accNo != null && accNo.matches("\\d+");
    }
    public static boolean isValidMenuChoice(String input, int min, int max) {
        try {
            int n = Integer.parseInt(input.trim());
            return n >= min && n <= max;
        } catch (Exception ex) {
            return false;
        }
    }
}

package com.bank.ui;
import java.util.List;
import java.util.Scanner;
import com.bank.db.AccountDatabase;
import com.bank.model.Account;

public class AdminMenu {
    private static final String ADMIN_PIN = "9999"; // admin PIN
    private final Scanner scanner = new Scanner(System.in);
    private final AccountDatabase accountDatabase = new AccountDatabase();

    public void run() {
        System.out.println("Enter Admin PIN:");
        int attempts = 3;
        while (attempts > 0) {
            String pin = scanner.nextLine().trim();
            if (pin.equals(ADMIN_PIN)) {
                adminPanel();
                return;
            } else {
                attempts--;
                System.out.println("Incorrect PIN. Attempts left: " + attempts);
                if (attempts == 0) {
                    System.out.println("Returning to Main Menu.");
                    return;
                }
            }
        }
    }

    private void adminPanel() {
        while (true) {
            System.out.println("\n--- Admin Panel ---");
            System.out.println("1. Show All Accounts");
            System.out.println("2. Back to Main Menu");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();
            if (!InputValidator.isValidMenuChoice(input, 1, 2)) {
                System.out.println("Invalid choice.");
                continue;
            }
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1 -> showAllAccounts();
                case 2 -> { return; }
            }
        }
    }

    private void showAllAccounts() {
        List<Account> accounts = accountDatabase.getAccountsByUserId(-1); 
        accounts = getAllAccounts();

        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.println("Account Number | User ID | UPI ID          | Balance   | Status");
        System.out.println("--------------------------------------------------------------");
        for (Account acc : accounts) {
            String status = (acc.getStatus() == 1) ? "ACTIVE" : "CLOSED";
            System.out.printf("%13d | %7d | %-15s | %9.2f | %s\n",
                    acc.getAccountId(), acc.getUserId(), acc.getUpiId(), acc.getBalance(), status);
        }
    }

    private List<Account> getAllAccounts() {
        return accountDatabase.getAllAccounts();
    }
}

package com.bank.ui;

import java.util.Scanner;



public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final UserMenu userMenu = new UserMenu();
    private final AdminMenu adminMenu = new AdminMenu();
   

    public void run() {
        while (true) {
        	    System.out.println("\n------------------------------");
            System.out.println("    Welcome to DproBank ");
            System.out.println("------------------------------");
            System.out.println("1.Admin Login");
            System.out.println("2.User Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();
            if (!InputValidator.isValidMenuChoice(input, 1, 3)) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1 -> adminMenu.run();
                case 2 -> userMenu.run();
                case 3 -> {
                    System.out.println("Thanks for using DproBank. Goodbye!");
                    return;
                }
            }
        }
    }
}

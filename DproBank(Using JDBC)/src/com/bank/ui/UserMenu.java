package com.bank.ui;

import java.util.List;
import java.util.Scanner;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.service.AccountService;
import com.bank.service.TransactionService;
import com.bank.service.UserService;


public class UserMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();
    private final TransactionService transactionService = new TransactionService();
   

    public void run() {
        while (true) {
            System.out.println("\n--- User Panel ---");
            System.out.println("1. Create New Account");
            System.out.println("2. Login Existing Account");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();
            if (!InputValidator.isValidMenuChoice(input, 1, 3)) {
                System.out.println("Invalid option.");
                continue;
            }
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1 -> createAccountFlow();
                case 2 -> loginFlow();
                case 3 -> { return; }
            }
        }
    }

    private void createAccountFlow() {
        String name = "";
        while (true) {
            System.out.println("\n--- Create Account: Step 1 ---");
            System.out.println("1. Enter Full Name");
            System.out.println("2. Cancel Account Creation");
            System.out.print("Select option: ");
            String option = scanner.nextLine().trim();

            if ("2".equals(option)) {
                System.out.println("Account creation cancelled.");
                return;
            } else if ("1".equals(option)) {
                System.out.print("Full Name: ");
                name = scanner.nextLine().trim();
                if (InputValidator.isValidName(name)) break;
                else System.out.println("Invalid name. Please try again.");
            } else {
                System.out.println("Invalid option.");
            }
        }

        String mobile = "";
        while (true) {
            System.out.println("\n--- Create Account: Step 2 ---");
            System.out.println("1. Enter Mobile Number");
            System.out.println("2. Cancel Account Creation");
            System.out.print("Select option: ");
            String option = scanner.nextLine().trim();

            if ("2".equals(option)) {
                System.out.println("Account creation cancelled.");
                return;
            } else if ("1".equals(option)) {
                System.out.print("Mobile Number: ");
                mobile = scanner.nextLine().trim();
                if (InputValidator.isValidMobile(mobile)) break;
                else System.out.println("Invalid mobile number. Please try again.");
            } else {
                System.out.println("Invalid option.");
            }
        }

        double initialDeposit = 0.0;
        while (true) {
            System.out.println("\n--- Create Account: Step 3 ---");
            System.out.println("1. Enter Initial Deposit Amount");
            System.out.println("2. Cancel Account Creation");
            System.out.print("Select option: ");
            String option = scanner.nextLine().trim();

            if ("2".equals(option)) {
                System.out.println("Account creation cancelled.");
                return;
            } else if ("1".equals(option)) {
                System.out.print("Initial Deposit Amount: ");
                String amtStr = scanner.nextLine().trim();
                if (InputValidator.isValidAmount(amtStr)) {
                    initialDeposit = Double.parseDouble(amtStr);
                    break;
                } else {
                    System.out.println("Invalid amount. Please try again.");
                }
            } else {
                System.out.println("Invalid option.");
            }
        }

        String pin = "";
        while (true) {
            System.out.println("\n--- Create Account: Step 4 ---");
            System.out.println("1. Set 4-digit PIN");
            System.out.println("2. Cancel Account Creation");
            System.out.print("Select option: ");
            String option = scanner.nextLine().trim();

            if ("2".equals(option)) {
                System.out.println("Account creation cancelled.");
                return;
            } else if ("1".equals(option)) {
                System.out.print("4-digit PIN: ");
                pin = scanner.nextLine().trim();
                if (!InputValidator.isValidPin(pin)) {
                    System.out.println("Invalid PIN. Must be exactly 4 digits.");
                    continue;
                }
                System.out.print("Confirm 4-digit PIN: ");
                String confirm = scanner.nextLine().trim();
                if (!pin.equals(confirm)) {
                    System.out.println("PINs do not match. Please try again.");
                    continue;
                }
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }

        // Find or register user, then create account
        int userId = userService.registerUser(name, mobile);
        if (userId == -1) {
            System.out.println("Mobile already registered.Create New Account");
            User existing = userService.getUserByMobile(mobile);
            if (existing != null) userId = existing.getUserId();
            else {
                System.out.println("user not found.");
                return;
            }
        }

        // Create the account
        Account account = accountService.createAccount(userId, mobile, pin, initialDeposit);
        if (account == null) {
            System.out.println("Account creation failed. Please try again.");
            return;
        }

        System.out.println("✅ Account created successfully!");
        System.out.printf("Your Account Number: %d\n", account.getAccountId());
        System.out.printf("Your UPI ID: %s\n", account.getUpiId());

        // Record initial deposit in transaction history, if not zero
        if (initialDeposit > 0) {
            transactionService.recordTransaction(
                account.getAccountId(),
                "Deposit",
                initialDeposit,
                "Initial Deposit",
                account.getBalance()
            );
        }
    }


    private void loginFlow() {
        while (true) {
            System.out.println("\n--- Login ---");
            System.out.println("1. Enter Account Number");
            System.out.println("2. Back to User Menu");
            System.out.print("Select option: ");
            String choice = scanner.nextLine().trim();
            if ("2".equals(choice)) {
                return; // Go back to user menu
            } else if (!"1".equals(choice)) {
                System.out.println("Invalid choice.");
                continue;
            }

            // Enter Account Number with validation
            System.out.print("Enter Account Number: ");
            String accStr = scanner.nextLine().trim();
            if (!InputValidator.isValidAccountNumber(accStr)) {
                System.out.println("Invalid account number format.");
                continue;
            }
            int accNo = Integer.parseInt(accStr);
            Account acc = accountService.getAccountById(accNo);
            if (acc == null || acc.getStatus() != 1) {
                System.out.println("Account not found or inactive.");
                continue;
            }

            // Enter PIN or Forgot PIN optios
            while (true) {
                System.out.println("\n1. Enter PIN");
                System.out.println("2. Forgot PIN");
                System.out.println("3. Back to Login Menu");
                System.out.print("Select option: ");
                String pinOption = scanner.nextLine().trim();

                if ("3".equals(pinOption)) {
                    break; 
                }

                if ("1".equals(pinOption)) {
                    int attempts = 3;
                    while (attempts > 0) {
                        System.out.print("Enter 4-digit PIN: ");
                        String pin = scanner.nextLine().trim();
                        if (acc.getPin().equals(pin)) {
                            System.out.println("✅ Login successful!");
                            dashboard(acc);
                            return;
                        } else {
                            attempts--;
                            if (attempts > 0)
                                System.out.println("Incorrect PIN."+ attempts+" Attempts left: " );
                        }
                    }
                    System.out.println("Too many incorrect PIN attempts. Returning to Login Menu.");
                    break; 
                } else if ("2".equals(pinOption)) {
                    forgotPin(acc);
                    return;
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            }
        }
    }


    private void forgotPin(Account acc) {
        System.out.println("\n--- Forgot PIN ---");
        
        while (true) {
            System.out.println("1. Enter registered mobile number");
            System.out.println("2. Back to Login Menu");
            System.out.print("Select option: ");
            String choice = scanner.nextLine().trim();
            
            if ("2".equals(choice)) {
                System.out.println("Returning to Login Menu.");
                return;
            } else if ("1".equals(choice)) {
                System.out.print("Enter your registered mobile number: ");
                String mobile = scanner.nextLine().trim();
                User user = userService.getUserById(acc.getUserId());
                
                if (user != null && user.getMobile().equals(mobile)) {
                    System.out.println("Mobile verified.");
                    
                    String newPin, confirmPin;
                    while (true) {
                        System.out.print("Enter new 4-digit PIN: ");
                        newPin = scanner.nextLine().trim();
                        if (!InputValidator.isValidPin(newPin)) {
                            System.out.println("Invalid PIN format. Try again.");
                            continue;
                        }
                        System.out.print("Confirm new PIN: ");
                        confirmPin = scanner.nextLine().trim();
                        if (!newPin.equals(confirmPin)) {
                            System.out.println("PINs do not match. Try again.");
                            continue;
                        }
                        break;
                    }
                    
                    boolean updated = accountService.updatePin(acc.getAccountId(), newPin);
                    if (updated) {
                        System.out.println("PIN reset successful! Please login again.");
                    } else {
                        System.out.println("PIN reset failed! Please try later.");
                    }
                    return;
                } else {
                    System.out.println("Mobile number does not match our records. Please try again.");
                }
            } else {
                System.out.println("Invalid option. Please select either 1 or 2.");
            }
        }
    }


    private void dashboard(Account acc) {
        while(true) {
        	User user = userService.getUserById(acc.getUserId());
            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.printf("   Welcome %s | AccountNo: %d | UPI: %s\n", user.getName(), acc.getAccountId(), acc.getUpiId()) ;
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Mini Statement");
            System.out.println("7. Account Settings");
            System.out.println("8. View Profile");
            System.out.println("9. Logout");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();
            if (!InputValidator.isValidMenuChoice(input, 1, 9)) {
                System.out.println("Invalid choice.");
                continue;
            }
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1 -> showBalance(acc);
                case 2 -> depositMoney(acc);
                case 3 -> withdrawMoney(acc);
                case 4 -> transferMoney(acc);
                case 5 -> showTransactionHistory(acc);
                case 6 -> showMiniStatement(acc);
                case 7 -> accountSettings(acc);
                case 8 -> viewProfile(acc);
                case 9 -> { System.out.println("Logged out."); return; }
            }
            // Refresh Account details each time
            acc = accountService.getAccountById(acc.getAccountId());
            if(acc == null || acc.getStatus() == 0){
                System.out.println("Account closed or invalid. Logging out.");
                return;
            }
        }
    }

    private void showBalance(Account acc) {
        System.out.println("Current Balance: ₹" + acc.getBalance());
    }

    private void depositMoney(Account acc) {
        while (true) {
            System.out.println("\n--- Deposit Money ---");
            System.out.println("1. Enter Amount");
            System.out.println("2. Back to Dashboard");
            System.out.print("Select option: ");
            String option = scanner.nextLine().trim();
            
            if ("2".equals(option)) {
                System.out.println("Returning to Dashboard.");
                return;
            } else if ("1".equals(option)) {
                System.out.print("Enter deposit amount: ");
                String amtStr = scanner.nextLine().trim();
                if (!InputValidator.isValidAmount(amtStr)) {
                    System.out.println("Invalid amount. Please try again.");
                    continue;
                }
                double amt = Double.parseDouble(amtStr);
                double newBalance = acc.getBalance() + amt;
                if (accountService.updateBalance(acc.getAccountId(), newBalance)) {
                    transactionService.recordTransaction(acc.getAccountId(), "Deposit", amt, "Deposit", newBalance);
                    System.out.println("Deposit successful.Current balance: ₹" + newBalance);
                    return;
                } else {
                    System.out.println("Deposit failed. Please try again.");
                }
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }


    private void withdrawMoney(Account acc) {
        while (true) {
            System.out.println("\n--- Withdraw Money ---");
            System.out.println("1. Enter Amount");
            System.out.println("2. Back to Dashboard");
            System.out.print("Select option: ");
            String option = scanner.nextLine().trim();

            if ("2".equals(option)) {
                System.out.println("Returning to Dashboard.");
                return;
            } else if ("1".equals(option)) {
                System.out.print("Enter withdrawal amount: ");
                String amtStr = scanner.nextLine().trim();

                if (!InputValidator.isValidAmount(amtStr)) {
                    System.out.println("Invalid amount. Please try again.");
                    continue;
                }

                double amt = Double.parseDouble(amtStr);

                if (amt > acc.getBalance()) {
                    System.out.println("Insufficient balance. Try again.");
                    continue;
                }

                double newBalance = acc.getBalance() - amt;
                if (accountService.updateBalance(acc.getAccountId(), newBalance)) {
                    transactionService.recordTransaction(acc.getAccountId(), "Withdrawal", amt, "Withdrawal", newBalance);
                    System.out.println("Withdrawal successful. New balance: ₹" + newBalance);
                } else {
                    System.out.println("Withdrawal failed. Please try again.");
                }
                return;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }



    private void transferMoney(Account acc) {
        // Select the account to transfer FROM,if multiplre accounts 
        List<Account> userAccounts = accountService.getUserAccounts(acc.getUserId());
        Account fromAccount = acc;

        if (userAccounts.size() > 1) {
            System.out.println("\n--- Select Account to TRANSFER FROM ---");
            for (int i = 0; i < userAccounts.size(); i++) {
                Account a = userAccounts.get(i);
                System.out.printf("%d. Account No: %d | UPI: %s | Balance: ₹%.2f | Status: %s\n",
                        i + 1, a.getAccountId(), a.getUpiId(), a.getBalance(),
                        a.getStatus() == 1 ? "ACTIVE" : "CLOSED");
            }
            System.out.printf("%d. Back to Dashboard\n", userAccounts.size() + 1);

            while (true) {
                System.out.print("Select option: ");
                String choice = scanner.nextLine().trim();

                if (!InputValidator.isValidMenuChoice(choice, 1, userAccounts.size() + 1)) {
                    System.out.println("Invalid choice. Please select a number between 1 and " + (userAccounts.size() + 1));
                    continue;
                }

                int selected = Integer.parseInt(choice);
                if (selected == userAccounts.size() + 1) {
                    System.out.println("Transfer cancelled. Returning to Dashboard.");
                    return;
                }

                fromAccount = userAccounts.get(selected - 1);
                if (fromAccount.getStatus() != 1) {
                    System.out.println("Selected account is inactive. Please select another.");
                    continue;
                }
                break;  
            }
        }

        // Select recipient by Account Number or UPI ID or Cancel
        Account toAccount = null;

        while (true) {
            System.out.println("\n--- Select Recipient ---");
            System.out.println("1. By Account Number");
            System.out.println("2. By UPI ID");
            System.out.println("3. Cancel Transfer and Back to Dashboard");
            System.out.print("Select option: ");
            String option = scanner.nextLine().trim();

            if ("3".equals(option)) {
                System.out.println("Transfer cancelled. Returning to Dashboard.");
                return;
            }

            if ("1".equals(option)) {
                
                while (true) {
                    System.out.println("\n--- Enter Recipient Account Number ---");
                    System.out.println("1. Enter Account Number");
                    System.out.println("2. Back to Recipient Selection");
                    System.out.print("Select option: ");
                    String accOption = scanner.nextLine().trim();

                    if ("2".equals(accOption)) {
                        break; 
                    } else if ("1".equals(accOption)) {
                        System.out.print("Enter recipient Account Number: ");
                        String accStr = scanner.nextLine().trim();

                        if (!InputValidator.isValidAccountNumber(accStr)) {
                            System.out.println("Invalid account number format. Try again.");
                            continue;
                        }

                        toAccount = accountService.getAccountById(Integer.parseInt(accStr));
                        if (toAccount == null) {
                            System.out.println("Account not found. Try again.");
                            continue;
                        }

                        if (toAccount.getStatus() != 1) {
                            System.out.println("Recipient account is inactive.");
                            continue;
                        }

                        if (toAccount.getAccountId() == fromAccount.getAccountId()) {
                            System.out.println("You cannot transfer to the same account.");
                            continue;
                        }

                        break;  
                    } else {
                        System.out.println("Invalid option. Please select 1 or 2.");
                    }
                }
                if (toAccount != null) break;  
            } else if ("2".equals(option)) {
                
                while (true) {
                    System.out.println("\n--- Enter Recipient UPI ID ---");
                    System.out.println("1. Enter UPI ID");
                    System.out.println("2. Back to Recipient Selection");
                    System.out.print("Select option: ");
                    String upiOption = scanner.nextLine().trim();

                    if ("2".equals(upiOption)) {
                        break;  
                    } else if ("1".equals(upiOption)) {
                        System.out.print("Enter recipient UPI ID: ");
                        String upi = scanner.nextLine().trim();

                        if (upi.isEmpty()) {
                            System.out.println("UPI ID cannot be empty. Try again.");
                            continue;
                        }

                        toAccount = accountService.findAccountByUpi(upi);
                        if (toAccount == null) {
                            System.out.println("Recipient UPI ID not found. Try again.");
                            continue;
                        }

                        if (toAccount.getStatus() != 1) {
                            System.out.println("Recipient account is inactive.");
                            continue;
                        }

                        if (toAccount.getAccountId() == fromAccount.getAccountId()) {
                            System.out.println("You cannot transfer to the same account.");
                            continue;
                        }
                        break;  
                    } else {
                        System.out.println("Invalid option. Please select 1 or 2.");
                    }
                }
                if (toAccount != null) 
                	break;  
            } else {
                System.out.println("Invalid option. Please select 1, 2 or 3.");
            }
        }

        // Enter transfer amount or cancel
        while (true) {
            System.out.println("\n--- Transfer Amount ---");
            System.out.println("1. Enter Amount");
            System.out.println("2. Cancel Transfer and Back to Dashboard");
            System.out.print("Select option: ");
            String amtOption = scanner.nextLine().trim();

            if ("2".equals(amtOption)) {
                System.out.println("Transfer cancelled. Returning to Dashboard.");
                return;
            }

            if ("1".equals(amtOption)) {
                System.out.print("Enter transfer amount: ");
                String amtStr = scanner.nextLine().trim();

                if (!InputValidator.isValidAmount(amtStr)) {
                    System.out.println("Invalid amount format. Please try again.");
                    continue;
                }

                double amount = Double.parseDouble(amtStr);
                if (amount > fromAccount.getBalance()) {
                    System.out.println("Insufficient balance. Available: ₹" + fromAccount.getBalance());
                    continue;
                }
             // After user enters the amount and it passes validation
                if (!verifyPin(fromAccount)) {
                    System.out.println("PIN verification failed. Transfer cancelled.");
                    return;
                }

              
                boolean debitSuccess = accountService.updateBalance(fromAccount.getAccountId(), fromAccount.getBalance() - amount);
                boolean creditSuccess = accountService.updateBalance(toAccount.getAccountId(), toAccount.getBalance() + amount);

                if (debitSuccess && creditSuccess) {
                    transactionService.recordTransaction(fromAccount.getAccountId(), "Transfer Debit", amount,
                            "Transfer to " + toAccount.getUpiId(), fromAccount.getBalance() - amount);
                    transactionService.recordTransaction(toAccount.getAccountId(), "Transfer Credit", amount,
                            "Transfer from " + fromAccount.getUpiId(), toAccount.getBalance() + amount);

                    System.out.println("Transfer successful!");
                    return;
                } else {
                    
                    System.out.println("Transfer failed. Rolling back changes.");
                    if (debitSuccess)
                        accountService.updateBalance(fromAccount.getAccountId(), fromAccount.getBalance());
                    if (creditSuccess)
                        accountService.updateBalance(toAccount.getAccountId(), toAccount.getBalance());
                    return;
                }
            } else {
                System.out.println("Invalid option. Please select 1 or 2.");
            }
        }
    }



    private boolean performTransfer(Account from, Account to, double amount) {
        double fromNewBal = from.getBalance() - amount;
        double toNewBal = to.getBalance() + amount;
       
        boolean fromUpdated = accountService.updateBalance(from.getAccountId(), fromNewBal);
        boolean toUpdated = accountService.updateBalance(to.getAccountId(), toNewBal);
        if (fromUpdated && toUpdated) {
            // Record transactions for both sides
            transactionService.recordTransaction(from.getAccountId(), "Transfer Debit", amount,
                    "Transfer to " + to.getUpiId(), fromNewBal);
            transactionService.recordTransaction(to.getAccountId(), "Transfer Credit", amount,
                    "Transfer from " + from.getUpiId(), toNewBal);
            return true;
        } else {
           
            if (fromUpdated) accountService.updateBalance(from.getAccountId(), from.getBalance());
            if (toUpdated) accountService.updateBalance(to.getAccountId(), to.getBalance());
            return false;
        }
    }

    private void showTransactionHistory(Account acc) {
        List<Transaction> txns = transactionService.getTransactionHistoryByAccount(acc.getAccountId());
        if (txns.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.println("TxnID | Date & Time          | Type            | Amount     | Details                  | Balance After");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        for (Transaction txn : txns) {
            System.out.printf("%5d | %-19s | %-15s | %10.2f | %-24s | %13.2f\n",
                    txn.getTxnId(),
                    txn.getTxnTime(),
                    txn.getTxnType(),
                    txn.getAmount(),
                    txn.getDetails(),
                    txn.getBalanceAfter());
        }
    }

    private void showMiniStatement(Account acc) {
        List<Transaction> txns = transactionService.getMiniStatement(acc.getAccountId());
        if (txns.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.println("Last 5 transactions:");
        showTransactionHistoryLimited(txns, 5);
    }

    private void showTransactionHistoryLimited(List<Transaction> txns, int limit) {
        System.out.println("TxnID | Date & Time          | Type            | Amount     | Details                  | Balance After");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < Math.min(limit, txns.size()); i++) {
            Transaction txn = txns.get(i);
            System.out.printf("%5d | %-19s | %-15s | %10.2f | %-24s | %13.2f\n",
                    txn.getTxnId(),
                    txn.getTxnTime(),
                    txn.getTxnType(),
                    txn.getAmount(),
                    txn.getDetails(),
                    txn.getBalanceAfter());
        }
    }

    private void accountSettings(Account acc) {
        User user = userService.getUserById(acc.getUserId());
        while(true) {
            System.out.println("\n--- Account Settings ---");
            System.out.println("1. Close Account");
            System.out.println("2. Update PIN");
            System.out.println("3. Update Name");
            System.out.println("4. Update Mobile Number");
            System.out.println("5. Back to Dashboard");
            System.out.print("Select option: ");
            
            String choice = scanner.nextLine().trim();
            if (!InputValidator.isValidMenuChoice(choice, 1, 5)) {
                System.out.println("Invalid option.");
                continue;
            }
            int ch = Integer.parseInt(choice);

            switch(ch) {
                case 1:
                    if(accountService.closeAccount(acc.getAccountId())){
                        System.out.println("Account closed successfully.");
                        return;
                    } else {
                        System.out.println("Failed to close account.");
                    }
                    break;
                case 2:
                    updatePin(acc);
                    break;
                case 3:
                    while(true){
                        System.out.print("Enter new name: ");
                        String newName = scanner.nextLine().trim();
                        if (!InputValidator.isValidName(newName)) {
                            System.out.println("Invalid name format. Please use letters, spaces, hyphens, or apostrophes.");
                            continue;
                        }
                        if(userService.updateUserName(user.getUserId(), newName)){
                            System.out.println("Name updated successfully.");
                            user.setName(newName);
                        } else {
                            System.out.println("Failed to update name.");
                        }
                        break;
                    }
                    break;
                case 4:
                    updateMobile(user);
                    break;
                case 5:
                    return; // Back to Dashboard
            }
        }
    }
    private void updateMobile(User user) {
        while (true) {
            System.out.print("Enter new 10-digit mobile number: ");
            String newMobile = scanner.nextLine().trim();

            if (!InputValidator.isValidMobile(newMobile)) {
                System.out.println("Invalid mobile number format. It should be exactly 10 digits. Please try again.");
                continue;
            }

            if (userService.updateUserMobile(user.getUserId(), newMobile)) {
                System.out.println("Mobile number updated successfully.");
                user.setMobile(newMobile);
                break;
            } else {
                System.out.println("Mobile number already in use or update failed. Please try another number.");
            }
        }
    }

    private void updatePin(Account acc) {
        while(true) {
            System.out.print("Enter new 4-digit PIN: ");
            String newPin = scanner.nextLine().trim();
            if(!InputValidator.isValidPin(newPin)){
                System.out.println("Invalid PIN format. Please enter exactly 4 digits.");
                continue;
            }
            System.out.print("Confirm new PIN: ");
            String confirmPin = scanner.nextLine().trim();
            if(!newPin.equals(confirmPin)) {
                System.out.println("PINs do not match. Try again.");
                continue;
            }
            if(accountService.updatePin(acc.getAccountId(), newPin)){
                System.out.println("PIN updated successfully.");
            } else {
                System.out.println("Failed to update PIN. Please try again later.");
            }
            break;
        }
    }
    private boolean verifyPin(Account acc) {
        int attempts = 3;
        while (attempts > 0) {
            System.out.print("Enter your 4-digit PIN to confirm: ");
            String inputPin = scanner.nextLine().trim();
            if (inputPin.equals(acc.getPin())) {
                return true;  // PIN correct
            } else {
                attempts--;
                System.out.println("Incorrect PIN. Attempts left: " + attempts);
            }
        }
        return false; // PIN verification failed
    }

    private void viewProfile(Account acc) {
        User user = userService.getUserById(acc.getUserId());
        List<Account> accounts = accountService.getUserAccounts(acc.getUserId());

        while(true) {
            System.out.println("\n--- Your Profile ---");
            System.out.printf("Name       : %s\n", user.getName());
            System.out.printf("Mobile No. : %s\n", user.getMobile());
            System.out.println("Your Accounts:");
            System.out.println("AccountNo | UPI ID              | Status  | Balance");
            System.out.println("--------------------------------------------------");
            for(Account a : accounts){
                System.out.printf("%9d | %-18s | %-7s | ₹%.2f\n",
                        a.getAccountId(), a.getUpiId(), a.getStatus() == 1 ? "ACTIVE" : "CLOSED", a.getBalance());
            }
            System.out.println("1. Back to Dashboard");
            System.out.print("Select option: ");
            String input = scanner.nextLine().trim();

            if("1".equals(input)) {
                return; // Return to Dashboard
            } else {
                System.out.println("Invalid option. Please enter 1 to return.");
            }
        }
    }

}

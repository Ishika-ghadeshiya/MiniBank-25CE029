
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Comparator;
import java.util.ArrayList;

import model.Account;
import model.Customer;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.Premium;

import service.Command;
import service.CommandParser;
import service.WithdrawRule;
import service.AccountWorker;
import service.UnsafeCounter;
import service.TransactionProcessor;
import service.TransactionBuffer;
import service.TransferCoordinator;
import service.StatePersister;
import service.TransactionLog;
import service.ReportGenerator;
import service.AccountStore;
import service.BankService;
import service.Repository;

import util.StatementFormatter;
import util.AnnotationValidator;
import util.BankResource;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import exception.AccountNotFoundException;

import static util.Validator.*;

record BankInfo(String name, String branch) {}

enum MenuOption {
    OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, STATEMENT, LIST_ACCOUNTS, REPORT, BATCH, EXIT
}

public class MiniBank {

    private static void deposit(Account account, long amount) {
        try {
            account.deposit(amount);
        } catch (InvalidAmountException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private static void withdraw(Account account, long amount) {
        try {
            account.withdraw(amount);
        } catch (InsufficientFundsException e) {
            System.out.println(
                "Withdrawal failed: " + e.getMessage()
                + " Shortfall: Rs. " + e.getShortfall()
            );
        } catch (InvalidAmountException e) {
            System.out.println(
                "Withdrawal failed: " + e.getMessage()
            );
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankInfo bank = new BankInfo(
            "MiniBank", "CHARUSAT Branch"
        );

        System.out.println("==============================");
        System.out.println(bank);
        System.out.println("==============================");

        Customer.Address address1 = new Customer.Address(
            "Station Road", "Anand", "388001"
        );

        Customer.Address address2 = new Customer.Address(
            "Race Course Road", "Vadodara", "390007"
        );

        Customer.Address address3 = new Customer.Address(
            "CG Road", "Ahmedabad", "380009"
        );

        Customer c1 = new Customer(
            "Riya", "riya@gmail.com", "9876543210", address1
        );

        Customer c2 = new Customer(
            "Aarav", "aarav@gmail.com", "9876543211", address2
        );

        Customer c3 = new Customer(
            "Neha", "neha@gmail.com", "9876543212", address3
        );

        System.out.println("\n===== CUSTOMER DETAILS =====");
        System.out.println(c1);
        System.out.println(c2);
        System.out.println(c3);

        Account[] accounts = {
            new SavingsAccount(c1.getName(), 5000, 1000),
            new CurrentAccount(c2.getName(), 10000, 5000),
            new FixedDepositAccount(c3.getName(), 15000)
        };

        System.out.println("\n===== ACCOUNT DETAILS =====");

        for (Account account : accounts) {
            System.out.println(account);
        }

        System.out.println("\n===== TRANSACTIONS =====");

        deposit(accounts[0], 2000);
        withdraw(accounts[0], 3000);
        withdraw(accounts[0], 10000);

        deposit(accounts[1], 3000);
        withdraw(accounts[1], 2000);

        deposit(accounts[2], 1500);
        withdraw(accounts[2], 500);

        System.out.println("\n===== UPDATED ACCOUNTS =====");

        for (Account account : accounts) {
            System.out.println(account);
        }

        System.out.println("\n===== ACCOUNT COMPARISON =====");

        Account a1 = accounts[0];
        Account a2 = accounts[1];
        Account a3 = accounts[0];

        System.out.println(
            "First and second account equal: " + a1.equals(a2)
        );

        System.out.println(
            "First and third account equal: " + a1.equals(a3)
        );

        System.out.println(
            "First account hash code: " + a1.hashCode()
        );

        Object obj = accounts[0];

        if (obj instanceof Account) {
            System.out.println("Object is an Account");
        }

        System.out.println("\n===== CUSTOMER COPY =====");

        Customer copiedCustomer = c1.clone();

        System.out.println("Original Customer: " + c1);
        System.out.println("Copied Customer: " + copiedCustomer);
        System.out.println(
            "Same object: " + (c1 == copiedCustomer)
        );

        System.out.println(accounts[0].getStatement());

        System.out.println("\n===== INPUT VALIDATION =====");

        System.out.println(
            "Valid Mobile (9876543210): "
            + isValidMobile("9876543210")
        );
        System.out.println(
            "Invalid Mobile (12345): "
            + isValidMobile("12345")
        );
        System.out.println(
            "Valid Email (riya@gmail.com): "
            + isValidEmail("riya@gmail.com")
        );
        System.out.println(
            "Invalid Email (riya@): "
            + isValidEmail("riya@")
        );
        System.out.println(
            "Valid PAN (ABCDE1234F): "
            + isValidPan("ABCDE1234F")
        );
        System.out.println(
            "Invalid PAN (ABC123): "
            + isValidPan("ABC123")
        );
        System.out.println(
            "Valid IFSC (SBIN0001234): "
            + isValidIfsc("SBIN0001234")
        );
        System.out.println(
            "Invalid IFSC (SBIN123): "
            + isValidIfsc("SBIN123")
        );

        System.out.println("\n===== COMMAND PARSER =====");

        try {
            Command command = CommandParser.parse(
                "DEPOSIT AC0001 500"
            );

            System.out.println(
                "Transaction Type: " + command.type()
            );
            System.out.println(
                "Account Number: " + command.accountNumber()
            );
            System.out.println(
                "Amount: Rs. " + command.amount()
            );

        } catch (IllegalArgumentException e) {
            System.out.println(
                "Command Error: " + e.getMessage()
            );
        }

        System.out.println(
            StatementFormatter.buildStatement(accounts[0])
        );

        System.out.println("\n===== ACCOUNT TYPES =====");

        for (Account account : accounts) {
            System.out.println(
                account.getAccountNumber()
                + " | " + account.getClass().getSimpleName()
                + " | Interest Rate: "
                + account.interestRate() + "%"
            );

            System.out.printf(
                "Monthly Interest: Rs. %.2f%n",
                account.monthlyInterest()
            );

            if (account instanceof SavingsAccount savings) {
                System.out.println(
                    "Minimum Balance: Rs. "
                    + savings.getMinBalance()
                );
            } else if (account instanceof CurrentAccount current) {
                System.out.println(
                    "Overdraft Limit: Rs. "
                    + current.getOverdraftLimit()
                );
            } else if (account instanceof FixedDepositAccount) {
                System.out.println(
                    "Withdrawal Status: Locked"
                );
            }

            System.out.println();
        }

        System.out.println("\n===== WITHDRAWAL RULES =====");

        System.out.println(
            "Savings account withdrawal of Rs. 3500:"
        );
        withdraw(accounts[0], 3500);

        System.out.println(
            "Savings account withdrawal of Rs. 500:"
        );
        withdraw(accounts[0], 500);

        System.out.println(
            "Current account withdrawal of Rs. 13000:"
        );
        withdraw(accounts[1], 13000);

        System.out.println(
            "Current account withdrawal of Rs. 4000:"
        );
        withdraw(accounts[1], 4000);

        System.out.println(
            "Fixed deposit withdrawal of Rs. 1000:"
        );
        withdraw(accounts[2], 1000);

        System.out.println("\n===== FINAL ACCOUNT DETAILS =====");

        for (Account account : accounts) {
            System.out.println(account);
        }

        System.out.println("\n===== BANKING INTERFACES =====");

        for (Account account : accounts) {
            System.out.println(
                account.getAccountNumber()
                + " | Yearly Interest: Rs. "
                + String.format("%.2f", account.yearlyInterest())
            );

            if (account instanceof Premium) {
                System.out.println(
                    "Premium Account: "
                    + account.getAccountNumber()
                );
            }
        }

        System.out.println("\n===== WITHDRAW RULE TEST =====");

        WithdrawRule anonymousRule = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.isActive()
                        && account.canWithdraw(amount);
            }
        };

        WithdrawRule lambdaRule = (account, amount) ->
                account.isActive()
                && account.canWithdraw(amount);

        Account savingsAccount = accounts[0];

        System.out.println(
            "Account: " + savingsAccount.getAccountNumber()
        );
        System.out.println("Requested Withdrawal: Rs. 1000");
        System.out.println(
            "Anonymous Class Allowed: "
            + anonymousRule.allow(savingsAccount, 1000)
        );
        System.out.println(
            "Lambda Allowed: "
            + lambdaRule.allow(savingsAccount, 1000)
        );

        System.out.println("\n===== ANNOTATION VALIDATION =====");

        Account validAccount =
            new SavingsAccount("Meera", 2000, 500);

        String[] validErrors =
            AnnotationValidator.validate(validAccount);

        System.out.println(
            "Valid Account Errors: " + validErrors.length
        );

        Account invalidAccount =
            new SavingsAccount("Test", 1000, 0);

        Account.setBalanceForValidationTest(
            invalidAccount, -100
        );

        String[] invalidErrors =
            AnnotationValidator.validate(invalidAccount);

        System.out.println(
            "Invalid Account Errors: " + invalidErrors.length
        );

        for (String error : invalidErrors) {
            System.out.println(error);
        }

        Account longNameAccount =
            new SavingsAccount("VeryLongCustomerName", 1000, 0);

        String[] nameErrors =
            AnnotationValidator.validate(longNameAccount);

        System.out.println(
            "Long Name Errors: " + nameErrors.length
        );

        for (String error : nameErrors) {
            System.out.println(error);
        }

        System.out.println("\n===== EXCEPTION HANDLING =====");

        Account testAccount =
            new SavingsAccount("Karan", 1000, 0);

        System.out.println(
            "Initial Balance: Rs. " + testAccount.getBalance()
        );

        try {
            testAccount.withdraw(5000);
        } catch (InsufficientFundsException e) {
            System.out.println(
                "Withdrawal failed: short by Rs. "
                + e.getShortfall()
            );
        } catch (InvalidAmountException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Withdrawal attempt completed.");
        }

        System.out.println(
            "Balance After Failed Withdrawal: Rs. "
            + testAccount.getBalance()
        );

        try {
            testAccount.deposit(-100);
        } catch (InvalidAmountException e) {
            System.out.println(
                "Invalid Deposit: " + e.getMessage()
            );
        }

        try {
            testAccount.withdraw(0);
        } catch (BankException e) {
            System.out.println(
                "Invalid Withdrawal: " + e.getMessage()
            );
        }

        try {
            throw new AccountNotFoundException("AC9999");
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n===== TRANSFER TEST =====");

        Account source =
            new SavingsAccount("Dev", 5000, 1000);

        Account destination =
            new CurrentAccount("Isha", 2000, 1000);

        try {
            source.transfer(destination, 1500);
        } catch (BankException e) {
            System.out.println(
                "Transfer Error: " + e.getMessage()
            );
        }

        System.out.println(
            "Source Balance: Rs. " + source.getBalance()
        );
        System.out.println(
            "Destination Balance: Rs. "
            + destination.getBalance()
        );

        try {
            source.transfer(destination, 10000);
        } catch (BankException e) {
            System.out.println(
                "Transfer Error: " + e.getMessage()
            );
        }

        System.out.println(
            "Source Balance After Failed Transfer: Rs. "
            + source.getBalance()
        );

        System.out.println("\n===== TRY WITH RESOURCES =====");

        try (BankResource resource = new BankResource()) {
            resource.process();
        }

        System.out.println("\n===== RACE CONDITION TEST =====");

        int threadCount = 10;
        int times = 10;

        try {
            int unsafeResult = UnsafeCounter.runTest(
                threadCount, times
            );

            System.out.println(
                "Expected Counter: " + (threadCount * times)
            );
            System.out.println(
                "Unsynchronized Counter: " + unsafeResult
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Counter test interrupted.");
        }

        System.out.println("\n===== MULTITHREADING =====");

        Account sharedAccount =
            new SavingsAccount("Worker", 1000, 0);

        long initialBalance = sharedAccount.getBalance();
        long depositAmount = 100;

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            AccountWorker worker = new AccountWorker(
                sharedAccount, times, depositAmount
            );

            threads[i] = new Thread(
                worker, "Worker-" + (i + 1)
            );
        }

        System.out.println(
            "Initial Balance: Rs. " + initialBalance
        );
        System.out.println(
            "Expected Final Balance: Rs. "
            + (initialBalance
                + threadCount * times * depositAmount)
        );

        for (Thread thread : threads) {
            thread.start();
        }

        boolean interrupted = false;

        for (Thread thread : threads) {
            boolean joined = false;

            while (!joined) {
                try {
                    thread.join();
                    joined = true;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        System.out.println(
            "Actual Final Balance: Rs. "
            + sharedAccount.getBalance()
        );
        System.out.println(
            "Balance Correct: "
            + (sharedAccount.getBalance()
                == initialBalance
                + threadCount * times * depositAmount)
        );

        System.out.println("\n===== THREAD POOL =====");

        Account poolAccount =
            new SavingsAccount("Pool", 5000, 0);

        TransactionProcessor processor =
            new TransactionProcessor();

        for (int i = 0; i < 8; i++) {
            final int taskId = i + 1;

            processor.submit(() -> {
                if (taskId % 2 == 0) {
                    deposit(poolAccount, 500);
                } else {
                    withdraw(poolAccount, 200);
                }

                System.out.println(
                    "Task " + taskId
                    + " completed by "
                    + Thread.currentThread().getName()
                );
            });
        }

        processor.stop();

        System.out.println(
            "Thread Pool Final Balance: Rs. "
            + poolAccount.getBalance()
        );

        System.out.println("\n===== PRODUCER CONSUMER =====");

        TransactionBuffer buffer = new TransactionBuffer(3);

        Account bufferAccount =
            new SavingsAccount("Buffer", 1000, 0);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 6; i++) {
                    final int id = i;

                    buffer.put(() -> {
                        deposit(bufferAccount, 100);
                        System.out.println(
                            "Processed Transaction: " + id
                        );
                    });

                    System.out.println(
                        "Produced Transaction: " + id
                    );
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Producer interrupted.");
            } finally {
                buffer.close();
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                Runnable task;

                while ((task = buffer.take()) != null) {
                    task.run();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Consumer interrupted.");
            }
        }, "Consumer");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Producer-consumer interrupted.");
        }

        System.out.println(
            "Producer Consumer Final Balance: Rs. "
            + bufferAccount.getBalance()
        );

        System.out.println("\n===== DEADLOCK TEST =====");

        TransferCoordinator.demonstrateDeadlock();

        System.out.println("\n===== DEADLOCK PREVENTION =====");

        Account transferA =
            new SavingsAccount("Alpha", 5000, 0);

        Account transferB =
            new SavingsAccount("Beta", 5000, 0);

        Thread transferOne = new Thread(() -> {
            try {
                TransferCoordinator.safeTransfer(
                    transferA, transferB, 500
                );
            } catch (BankException e) {
                System.out.println(
                    "Transfer A to B failed: " + e.getMessage()
                );
            }
        }, "Transfer-A-B");

        Thread transferTwo = new Thread(() -> {
            try {
                TransferCoordinator.safeTransfer(
                    transferB, transferA, 300
                );
            } catch (BankException e) {
                System.out.println(
                    "Transfer B to A failed: " + e.getMessage()
                );
            }
        }, "Transfer-B-A");

        transferOne.start();
        transferTwo.start();

        try {
            transferOne.join();
            transferTwo.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Transfer test interrupted.");
        }

        System.out.println(
            "Account A Balance: Rs. "
            + transferA.getBalance()
        );
        System.out.println(
            "Account B Balance: Rs. "
            + transferB.getBalance()
        );
        System.out.println(
            "Total Balance: Rs. "
            + (transferA.getBalance() + transferB.getBalance())
        );

        System.out.println("\n===== FILE PERSISTENCE =====");

        Path dataFolder = Path.of("data");
        Path accountsFile = dataFolder.resolve("demonstration_accounts.dat");
        Path logsFolder = dataFolder.resolve("logs");
        Path logFile = logsFolder.resolve("demonstration_transactions.log");
        Path reportFile = dataFolder.resolve("demonstration_report.txt");

        try {
            StatePersister.save(accounts, accountsFile);
            System.out.println(
                "Accounts saved: " + accountsFile
            );

            Account[] loadedAccounts =
                StatePersister.load(accountsFile);

            System.out.println(
                "Accounts loaded: " + loadedAccounts.length
            );

            for (Account account : loadedAccounts) {
                System.out.println(account);
            }

            boolean balancesMatch =
                loadedAccounts.length == accounts.length;

            if (balancesMatch) {
                for (int i = 0; i < accounts.length; i++) {
                    if (!accounts[i].getAccountNumber().equals(
                            loadedAccounts[i].getAccountNumber())
                            || accounts[i].getBalance()
                            != loadedAccounts[i].getBalance()) {
                        balancesMatch = false;
                        break;
                    }
                }
            }

            System.out.println(
                "Saved and loaded balances match: "
                + balancesMatch
            );

            Files.createDirectories(logsFolder);
            Files.deleteIfExists(logFile);

            TransactionLog.append(
                logFile, "DEPOSIT AC0001 2000"
            );
            TransactionLog.append(
                logFile, "WITHDRAW AC0001 3000"
            );
            TransactionLog.append(
                logFile, "DEPOSIT AC0002 3000"
            );
            TransactionLog.append(
                logFile, "WITHDRAW AC0002 2000"
            );
            TransactionLog.append(
                logFile, "DEPOSIT AC0003 1500"
            );
            TransactionLog.append(
                logFile, "WITHDRAW AC0002 13000"
            );
            TransactionLog.append(
                logFile, "WITHDRAW AC0001 500"
            );

            System.out.println(
                "Transaction log saved: " + logFile
            );

            ReportGenerator.generate(
                logsFolder, reportFile
            );

            System.out.println(
                "Report saved: " + reportFile
            );

            for (String line : Files.readAllLines(reportFile)) {
                if (line.startsWith("File:")) {
                    break;
                }

                System.out.println(line);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(
                "File Error: " + e.getMessage()
            );
        }

        System.out.println("\n===== ACCOUNT COLLECTIONS =====");

        AccountStore store = new AccountStore();

        for (Account account : accounts) {
            store.add(account);
        }

        System.out.println(
            "Accounts in Store: " + store.size()
        );

        System.out.println(
            "Account Lookup: "
            + store.get(accounts[0].getAccountNumber())
        );

        System.out.println("Accounts Sorted by Balance:");

        for (Account account : store.sortedByBalance()) {
            System.out.println(account);
        }

        System.out.println("Accounts Sorted by Account Number:");

        for (Account account : store.sortedByAccountNumber()) {
            System.out.println(account);
        }

        store.addTransaction("DEPOSIT AC0001 2000");
        store.addTransaction("WITHDRAW AC0001 3000");
        store.addTransaction("DEPOSIT AC0002 3000");

        System.out.println("Transaction History:");

        for (String transaction : store.getTransactionHistory()) {
            System.out.println(transaction);
        }

        System.out.println("Accounts with Balance Above Rs. 3000:");

        for (Account account : store.balanceAbove(3000)) {
            System.out.println(account);
        }

        System.out.println("\n===== CONCURRENT ACCOUNT STORE =====");

        TransactionProcessor concurrentProcessor =
            new TransactionProcessor();

        for (Account account : accounts) {
            concurrentProcessor.addAccount(account);
        }

        System.out.println(
            "Concurrent Accounts: "
            + concurrentProcessor.getAccountCount()
        );

        System.out.println(
            "Concurrent Account Lookup: "
            + concurrentProcessor.getAccount(
                accounts[1].getAccountNumber()
            )
        );

        concurrentProcessor.stop();

        System.out.println("\n===== GENERIC REPOSITORY =====");
        Repository<Account> genericRepository = new Repository<>();
        genericRepository.saveAll(accounts);
        System.out.println("Accounts Saved: " + genericRepository.size());
        System.out.println("Account Lookup: "
                + genericRepository.findById(accounts[0].getAccountNumber()));
        System.out.println("All Accounts:");
        ArrayList<Account> repositoryAccounts = genericRepository.findAll();
        repositoryAccounts.sort(Comparator.comparing(Account::getAccountNumber));
        for (Account account : repositoryAccounts) {
            System.out.println(account);
        }

        BankService banking = new BankService();
        Path liveAccountsFile = dataFolder.resolve("accounts.dat");
        try {
            if (Files.exists(liveAccountsFile)) {
                Account[] savedAccounts = StatePersister.load(liveAccountsFile);
                for (Account account : savedAccounts) {
                    banking.addAccount(account);
                }
                System.out.println("Saved accounts loaded: " + banking.accountCount());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Unable to load saved accounts: " + e.getMessage());
        }

        try {
            boolean running = true;
            while (running) {
                System.out.println("\n===== MINIBANK MENU =====");
                System.out.println("1. Open Account");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. Transfer");
                System.out.println("5. Exit");
                System.out.println("6. Account Statement");
                System.out.println("7. List Accounts");
                System.out.println("8. Generate Report");
                System.out.println("9. Process Transaction Batch");
                System.out.print("Enter your choice: ");

                if (!sc.hasNextLine()) {
                    break;
                }
                String input = sc.nextLine().trim();
                int choice;
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input!");
                    continue;
                }

                try {
                    switch (choice) {
                        case 1 -> {
                            System.out.print("Account type (SAVINGS/CURRENT/FIXED): ");
                            String type = sc.nextLine().trim();
                            System.out.print("Account holder name: ");
                            String name = sc.nextLine().trim();
                            System.out.print("Opening balance: ");
                            long balance = Long.parseLong(sc.nextLine().trim());
                            Account account = banking.openAccount(type, name, balance);
                            System.out.println("Account opened: " + account);
                        }
                        case 2 -> {
                            System.out.print("Account number: ");
                            String number = sc.nextLine().trim().toUpperCase();
                            System.out.print("Deposit amount: ");
                            long amount = Long.parseLong(sc.nextLine().trim());
                            banking.deposit(number, amount);
                        }
                        case 3 -> {
                            System.out.print("Account number: ");
                            String number = sc.nextLine().trim().toUpperCase();
                            System.out.print("Withdrawal amount: ");
                            long amount = Long.parseLong(sc.nextLine().trim());
                            banking.withdraw(number, amount);
                        }
                        case 4 -> {
                            System.out.print("Source account number: ");
                            String from = sc.nextLine().trim().toUpperCase();
                            System.out.print("Destination account number: ");
                            String to = sc.nextLine().trim().toUpperCase();
                            System.out.print("Transfer amount: ");
                            long amount = Long.parseLong(sc.nextLine().trim());
                            banking.transfer(from, to, amount);
                        }
                        case 5 -> {
                            System.out.println("Thank you for using MiniBank!");
                            running = false;
                        }
                        case 6 -> {
                            System.out.print("Account number: ");
                            System.out.println(banking.statement(
                                    sc.nextLine().trim().toUpperCase()));
                        }
                        case 7 -> {
                            System.out.println("===== ALL ACCOUNTS =====");
                            for (Account account : banking.listAccounts()) {
                                System.out.println(account);
                            }
                        }
                        case 8 -> {
                            Path liveReport = dataFolder.resolve("end_of_day_report.txt");
                            ReportGenerator.generate(logsFolder, liveReport);
                            System.out.println("Report saved: " + liveReport);
                            for (String line : Files.readAllLines(liveReport)) {
                                if (line.startsWith("File:")) break;
                                System.out.println(line);
                            }
                        }
                        case 9 -> {
                            TransactionProcessor batchProcessor =
                                    new TransactionProcessor();
                            for (Account account : banking.listAccounts()) {
                                batchProcessor.addAccount(account);
                            }
                            System.out.print("Account number for batch deposits: ");
                            String number = sc.nextLine().trim().toUpperCase();
                            Account batchAccount = banking.findAccount(number);
                            for (int i = 0; i < 5; i++) {
                                batchProcessor.submit(() -> {
                                    try {
                                        batchAccount.deposit(100);
                                    } catch (InvalidAmountException e) {
                                        System.out.println(e.getMessage());
                                    }
                                });
                            }
                            batchProcessor.stop();
                            System.out.println("Batch completed. Balance: Rs. "
                                    + batchAccount.getBalance());
                        }
                        default -> System.out.println("Invalid choice!");
                    }
                } catch (BankException | IllegalArgumentException | IOException e) {
                    System.out.println("Operation failed: " + e.getMessage());
                }
            }
        } finally {
            try {
                Files.createDirectories(dataFolder);
                ArrayList<Account> liveAccounts = banking.listAccounts();
                StatePersister.save(
                        liveAccounts.toArray(new Account[0]), liveAccountsFile);
                System.out.println("Accounts saved: " + liveAccountsFile);
            } catch (IOException e) {
                System.out.println("Unable to save accounts: " + e.getMessage());
            }
            sc.close();
        }
    }
}

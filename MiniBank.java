
import java.util.Scanner;

record BankInfo(String name, String branch) {}

enum MenuOption {
    OPEN_ACCOUNT,
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    EXIT
}

public class MiniBank {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankInfo bank = new BankInfo(
            "MiniBank",
            "CHARUSAT Branch"
        );

        System.out.println("==============================");
        System.out.println(bank);
        System.out.println("==============================");

      

        Customer c1 = new Customer(
            "Riya", "riya@gmail.com", "9876543210"
        );

        Customer c2 = new Customer(
            "Aarav", "aarav@gmail.com", "9876543211"
        );

        Customer c3 = new Customer(
            "Neha", "neha@gmail.com", "9876543212"
        );

        System.out.println("\nCustomer Details:");
        System.out.println(c1.getCustomerId() + " - " + c1.getName());
        System.out.println(c2.getCustomerId() + " - " + c2.getName());
        System.out.println(c3.getCustomerId() + " - " + c3.getName());

        Account[] accounts = {
            new Account(c1.getName(), 5000),
            new Account(c2.getName(), 10000),
            new Account(c3.getName())
        };

        System.out.println("\nInitial Account Details:");

        for (Account account : accounts) {
            displayAccount(account);
        }

        System.out.println("\n===== TRANSACTIONS =====");

        System.out.println("\nRiya's Account:");
        accounts[0].deposit(2000);
        System.out.println("Balance: Rs. " + accounts[0].getBalance());

        boolean result1 = accounts[0].withdraw(3000);
        System.out.println("Withdrawal status: " + result1);
        System.out.println("Balance: Rs. " + accounts[0].getBalance());

        boolean result2 = accounts[0].withdraw(10000);
        System.out.println("Withdrawal status: " + result2);
        System.out.println("Balance: Rs. " + accounts[0].getBalance());

        System.out.println("\nAarav's Account:");
        accounts[1].deposit(3000);
        accounts[1].withdraw(2000);

        System.out.println("\nNeha's Account:");
        accounts[2].deposit(1500);
        accounts[2].withdraw(500);

        System.out.println("\n===== FINAL ACCOUNT BALANCES =====");

        for (Account account : accounts) {
            displayAccount(account);
        }

        int choice = 0;

        do {
            System.out.println("\n===== MINIBANK MENU =====");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input!");
                sc.next();
                continue;
            }

            choice = sc.nextInt();

            MenuOption option = switch (choice) {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                case 5 -> MenuOption.EXIT;
                default -> null;
            };

            if (option == null) {
                System.out.println("Invalid choice!");
                continue;
            }

            String message = switch (option) {
                case OPEN_ACCOUNT ->
                    "Open Account - Available in a later practical";
                case DEPOSIT ->
                    "Deposit - Demonstrated above";
                case WITHDRAW ->
                    "Withdraw - Demonstrated above";
                case TRANSFER ->
                    "Transfer - To be implemented later";
                case EXIT ->
                    "Thank you for using MiniBank!";
            };

            System.out.println(message);

        } while (choice != 5);

        sc.close();
    }

    public static void displayAccount(Account account) {
        System.out.println(
            account.getAccountNumber()
            + " | " + account.getOwnerName()
            + " | Balance: Rs. " + account.getBalance()
            + " | Active: " + account.isActive()
        );
    }
}

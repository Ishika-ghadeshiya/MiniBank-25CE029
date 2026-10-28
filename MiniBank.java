
import java.util.Scanner;

// Record for bank information
record BankInfo(String name, String branch) {}

// Enum for menu options
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
                "MiniBank", "Anand"
        );

        System.out.println("======================");
        System.out.println("Welcome to " + bank.name());
        System.out.println("Branch: " + bank.branch());
        System.out.println("======================");

        boolean running = true;

        while (running) {

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

            int choice = sc.nextInt();

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

            switch (option) {

                case OPEN_ACCOUNT ->
                    System.out.println(
                        "Open Account - Coming soon"
                    );

                case DEPOSIT ->
                    System.out.println(
                        "Deposit - Coming soon"
                    );

                case WITHDRAW ->
                    System.out.println(
                        "Withdraw - Coming soon"
                    );

                case TRANSFER ->
                    System.out.println(
                        "Transfer - Coming soon"
                    );

                case EXIT -> {
                    System.out.println(
                        "Thank you for using MiniBank!"
                    );
                    running = false;
                }
            }
        }

        sc.close();
    }
}

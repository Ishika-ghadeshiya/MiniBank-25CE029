
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

        System.out.println("==========================");
        System.out.println(bank);
        System.out.println("==========================");

        int choice=0;

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
                    "Open Account - To be implemented later";
                case DEPOSIT ->
                    "Deposit - To be implemented later";
                case WITHDRAW ->
                    "Withdraw - To be implemented later";
                case TRANSFER ->
                    "Transfer - To be implemented later";
                case EXIT ->
                    "Thank you for using MiniBank!";
            };

            System.out.println(message);

        } while (choice != 5);

        sc.close();
    }
}


public class Account {

    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active;

    private static long accountCounter = 0;

    private static String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = Math.max(0, openingBalance);
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    public void deposit(long amount) {
        if (active && amount > 0 && balance <= Long.MAX_VALUE - amount) {
            balance += amount;
            System.out.println("Deposit successful: Rs. " + amount);
        } else {
            System.out.println("Invalid deposit amount or inactive account.");
        }
    }

    public boolean withdraw(long amount) {
        if (active && amount > 0 && balance >= amount) {
            balance -= amount;
            System.out.println("Withdrawal successful: Rs. " + amount);
            return true;
        }

        System.out.println("Withdrawal failed: Invalid amount or insufficient balance.");
        return false;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }
}

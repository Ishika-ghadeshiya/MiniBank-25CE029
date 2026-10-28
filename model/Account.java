
package model;

import java.io.Serializable;
import java.util.Objects;
import java.lang.reflect.Field;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import exception.AccountNotFoundException;

import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;

import util.StatementFormatter;

public abstract class Account
        implements Transactable, InterestBearing,
                   Serializable, Comparable<Account> {

    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;

    @MaxLength(10)
    private String ownerName;

    @Positive
    private long balance;

    private boolean active;

    private static long accountCounter = 0;

    private static synchronized String generateAccountNumber() {
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

    @Override
    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    public double monthlyInterest() {
        return yearlyInterest() / 12.0;
    }

    @Override
    public synchronized void deposit(long amount)
            throws InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                "Deposit amount must be greater than zero."
            );
        }

        if (!active) {
            throw new InvalidAmountException(
                "Cannot deposit into an inactive account."
            );
        }

        if (balance > Long.MAX_VALUE - amount) {
            throw new InvalidAmountException(
                "Deposit would exceed the allowed balance."
            );
        }

        balance += amount;
        System.out.println("Deposit successful: Rs. " + amount);
    }

    @Override
    public synchronized boolean withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                "Withdrawal amount must be greater than zero."
            );
        }

        if (!active) {
            throw new InvalidAmountException(
                "Cannot withdraw from an inactive account."
            );
        }

        if (balance < Long.MIN_VALUE + amount) {
            throw new InsufficientFundsException(
                "Withdrawal would exceed the allowed balance.",
                amount
            );
        }

        if (!canWithdraw(amount)) {
            long shortfall;

            if (this instanceof SavingsAccount savings) {
                long available = Math.max(
                    0, balance - savings.getMinBalance()
                );
                shortfall = Math.max(0, amount - available);

            } else if (this instanceof CurrentAccount current) {
                long available = Math.max(
                    0, balance + current.getOverdraftLimit()
                );
                shortfall = Math.max(0, amount - available);

            } else {
                shortfall = amount;
            }

            throw new InsufficientFundsException(
                "Withdrawal not allowed for this account.",
                shortfall
            );
        }

        balance -= amount;
        System.out.println("Withdrawal successful: Rs. " + amount);
        return true;
    }

    public void transfer(Account to, long amount)
            throws BankException {

        try {
            if (to == null) {
                throw new AccountNotFoundException("Destination account");
            }

            if (to == this) {
                throw new InvalidAmountException(
                    "Cannot transfer to the same account."
                );
            }

            if (amount <= 0) {
                throw new InvalidAmountException(
                    "Transfer amount must be greater than zero."
                );
            }

            synchronized (Account.class) {
                if (!to.isActive()) {
                    throw new InvalidAmountException(
                        "Destination account is inactive."
                    );
                }

                if (to.getBalance() > Long.MAX_VALUE - amount) {
                    throw new InvalidAmountException(
                        "Destination balance limit exceeded."
                    );
                }

                withdraw(amount);

                try {
                    to.deposit(amount);
                } catch (InvalidAmountException e) {
                    balance += amount;
                    throw e;
                }
            }

            System.out.println(
                "Transfer successful: Rs. " + amount
            );

        } catch (BankException e) {
            System.out.println(
                "Transfer failed: " + e.getMessage()
            );
            throw e;

        } finally {
            System.out.println("Transfer attempt completed.");
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public int compareTo(Account other) {
        return accountNumber.compareTo(other.accountNumber);
    }

    public static void setBalanceForValidationTest(
            Account account, long testBalance) {

        try {
            Field field = Account.class.getDeclaredField("balance");
            field.setAccessible(true);
            field.setLong(account, testBalance);

        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                "Unable to prepare validation test.", e
            );
        }
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName
                + " | Balance: Rs. " + balance
                + " | Active: " + active;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Account)) {
            return false;
        }

        Account other = (Account) obj;
        return Objects.equals(
            accountNumber, other.accountNumber
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    public String getStatement() {
        return StatementFormatter.buildStatement(this);
    }
}

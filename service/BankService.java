package service;

import java.util.ArrayList;
import java.util.Comparator;
import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import util.Validator;

public class BankService {
    private final Repository<Account> repository = new Repository<>();

    public synchronized Account openAccount(String type, String ownerName,
                                             long openingBalance) throws InvalidAmountException {
        if (ownerName == null || ownerName.isBlank() || ownerName.length() > 10) {
            throw new IllegalArgumentException("Account holder name must contain 1 to 10 characters.");
        }
        if (openingBalance < 0) {
            throw new InvalidAmountException("Opening balance cannot be negative.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Invalid account type.");
        }
        String normalizedType = type.trim().toUpperCase();
        if (!normalizedType.equals("SAVINGS") && !normalizedType.equals("CURRENT")
                && !normalizedType.equals("FIXED")) {
            throw new IllegalArgumentException("Invalid account type.");
        }

        Account account;
        do {
            account = switch (normalizedType) {
                case "SAVINGS" -> new SavingsAccount(ownerName, openingBalance, 1000);
                case "CURRENT" -> new CurrentAccount(ownerName, openingBalance, 5000);
                default -> new FixedDepositAccount(ownerName, openingBalance);
            };
        } while (repository.findById(account.getAccountNumber()) != null);

        repository.save(account);
        return account;
    }

    public synchronized void addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        repository.save(account);
    }

    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    public void deposit(String accountNumber, long amount) throws BankException {
        validateAmount(amount);
        findAccount(accountNumber).deposit(amount);
    }

    public void withdraw(String accountNumber, long amount) throws BankException {
        validateAmount(amount);
        findAccount(accountNumber).withdraw(amount);
    }

    public void transfer(String fromAccount, String toAccount, long amount) throws BankException {
        validateAmount(amount);
        findAccount(fromAccount).transfer(findAccount(toAccount), amount);
    }

    public String statement(String accountNumber) throws AccountNotFoundException {
        return findAccount(accountNumber).getStatement();
    }

    public ArrayList<Account> listAccounts() {
        ArrayList<Account> result = repository.findAll();
        result.sort(Comparator.comparing(Account::getAccountNumber));
        return result;
    }

    public int accountCount() {
        return repository.size();
    }

    private void validateAmount(long amount) throws InvalidAmountException {
        if (!Validator.isValidAmount(Long.toString(amount))) {
            throw new InvalidAmountException("Amount must be a positive whole number.");
        }
    }
}

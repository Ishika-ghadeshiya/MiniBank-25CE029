
import java.util.ArrayList;
import java.util.Comparator;

import model.Account;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;

import service.Repository;
import util.GenericCalculator;

public class RepositoryTest {

    public static void main(String[] args) {

        System.out.println("===== GENERIC REPOSITORY =====");

        Repository<Account> repository = new Repository<>();

        Account savings = new SavingsAccount("Riya", 5000, 1000);
        Account current = new CurrentAccount("Aarav", 10000, 5000);
        Account fixed = new FixedDepositAccount("Neha", 15000);

        repository.saveAll(savings, current, fixed);

        System.out.println(
            "Accounts Saved: " + repository.size()
        );

        System.out.println(
            "Account Lookup: "
            + repository.findById(savings.getAccountNumber())
        );

        System.out.println("All Accounts:");

        ArrayList<Account> allAccounts = repository.findAll();
        allAccounts.sort(Comparator.naturalOrder());

        for (Account account : allAccounts) {
            System.out.println(account);
        }

        System.out.println(
            "Delete Account: "
            + repository.delete(current.getAccountNumber())
        );

        System.out.println(
            "Accounts After Delete: " + repository.size()
        );

        System.out.println(
            "Deleted Account Found: "
            + (repository.findById(current.getAccountNumber()) != null)
        );

        System.out.println("\n===== GENERIC AVERAGE =====");

        Integer[] integers = {10, 20, 30};
        Double[] doubles = {10.5, 20.5, 30.5};
        Long[] longs = {100L, 200L, 300L};

        System.out.printf(
            "Integer Average: %.2f%n",
            GenericCalculator.average(integers)
        );

        System.out.printf(
            "Double Average: %.2f%n",
            GenericCalculator.average(doubles)
        );

        System.out.printf(
            "Long Average: %.2f%n",
            GenericCalculator.average(longs)
        );
    }
}

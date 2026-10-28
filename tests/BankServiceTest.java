
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import exception.InsufficientFundsException;
import model.Account;
import service.BankService;

public class BankServiceTest {

    @Test
    void depositIncreasesBalance() throws Exception {
        BankService bank = new BankService();

        Account account = bank.openAccount("SAVINGS", "Test", 5000);
        String number = account.getAccountNumber();

        bank.deposit(number, 1000);

        assertEquals(6000, bank.findAccount(number).getBalance());
    }

    @Test
    void withdrawalBeyondLimitThrowsException() throws Exception {
        BankService bank = new BankService();

        Account account = bank.openAccount("SAVINGS", "Test", 5000);
        String number = account.getAccountNumber();

        assertThrows(
                InsufficientFundsException.class,
                () -> bank.withdraw(number, 5000)
        );

        assertEquals(5000, account.getBalance());
    }

    @Test
    void concurrentDepositsMaintainCorrectBalance() throws Exception {
        BankService bank = new BankService();

        Account account = bank.openAccount("SAVINGS", "Test", 5000);
        String number = account.getAccountNumber();

        int threads = 10;
        int depositsPerThread = 10;
        long amount = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        try {
            List<Callable<Void>> tasks = new ArrayList<>();

            for (int i = 0; i < threads; i++) {
                tasks.add(() -> {
                    for (int j = 0; j < depositsPerThread; j++) {
                        bank.deposit(number, amount);
                    }
                    return null;
                });
            }

            List<Future<Void>> results = executor.invokeAll(tasks);

            for (Future<Void> result : results) {
                result.get();
            }

            long expectedBalance =
                    5000 + threads * depositsPerThread * amount;

            assertEquals(expectedBalance, account.getBalance());

        } finally {
            executor.shutdownNow();
        }
    }
}

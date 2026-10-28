
package service;

import util.Validator;

public class CommandParser {

    public static Command parse(String line) {

        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException(
                "Command cannot be empty."
            );
        }

        String[] parts = line.trim().split("\\s+");

        if (parts.length != 3) {
            throw new IllegalArgumentException(
                "Invalid command format. Use: TYPE ACCOUNT AMOUNT"
            );
        }

        TransactionType type;

        try {
            type = TransactionType.valueOf(
                parts[0].toUpperCase()
            );

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid transaction type."
            );
        }

        String accountNumber = parts[1].toUpperCase();

        if (!accountNumber.matches("AC[0-9]{4,}")) {
            throw new IllegalArgumentException(
                "Invalid account number."
            );
        }

        if (!Validator.isValidAmount(parts[2])) {
            throw new IllegalArgumentException(
                "Amount must be a positive whole number."
            );
        }

        long amount = Long.parseLong(parts[2]);

        return new Command(
            type,
            accountNumber,
            amount
        );
    }
}


package util;

import model.Account;

public class StatementFormatter {

    public static String buildStatement(Account account) {

        if (account == null) {
            throw new IllegalArgumentException(
                "Account cannot be null."
            );
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\n===== ACCOUNT STATEMENT =====\n");

        sb.append("Account Number: ")
          .append(account.getAccountNumber())
          .append("\n");

        sb.append("Account Holder: ")
          .append(account.getOwnerName())
          .append("\n");

        sb.append("Balance: Rs. ")
          .append(account.getBalance())
          .append("\n");

        sb.append("Status: ")
          .append(account.isActive() ? "Active" : "Inactive")
          .append("\n");

        sb.append("=============================\n");

        return sb.toString();
    }
}

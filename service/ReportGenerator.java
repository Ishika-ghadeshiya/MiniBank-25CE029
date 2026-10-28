
package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class ReportGenerator {

    public static void generate(Path logsFolder, Path reportFile)
            throws IOException {

        long totalDeposits = 0;
        long totalWithdrawals = 0;

        StringBuilder details = new StringBuilder();

        Files.createDirectories(logsFolder);

        try (DirectoryStream<Path> files =
                Files.newDirectoryStream(logsFolder)) {

            for (Path file : files) {

                if (!Files.isRegularFile(file)) {
                    continue;
                }

                BasicFileAttributes attributes =
                    Files.readAttributes(
                        file,
                        BasicFileAttributes.class
                    );

                details.append("File: ")
                    .append(file.getFileName())
                    .append(System.lineSeparator());

                details.append("Size: ")
                    .append(attributes.size())
                    .append(" bytes")
                    .append(System.lineSeparator());

                details.append("Last Modified: ")
                    .append(attributes.lastModifiedTime())
                    .append(System.lineSeparator());

                try (BufferedReader reader =
                        Files.newBufferedReader(
                            file,
                            StandardCharsets.UTF_8)) {

                    String line;

                    while ((line = reader.readLine()) != null) {

                        String[] parts = line.trim().split("\\s+");

                        if (parts.length != 3) {
                            continue;
                        }

                        long amount;

                        try {
                            amount = Long.parseLong(parts[2]);
                        } catch (NumberFormatException e) {
                            continue;
                        }

                        if (amount <= 0) {
                            continue;
                        }

                        if (parts[0].equalsIgnoreCase("DEPOSIT")) {
                            totalDeposits =
                                Math.addExact(totalDeposits, amount);

                        } else if (
                            parts[0].equalsIgnoreCase("WITHDRAW")
                        ) {
                            totalWithdrawals =
                                Math.addExact(totalWithdrawals, amount);
                        }
                    }
                }
            }
        }

        long netChange =
            Math.subtractExact(totalDeposits, totalWithdrawals);

        StringBuilder report = new StringBuilder();

        report.append("===== END OF DAY REPORT =====")
            .append(System.lineSeparator());

        report.append("Total Deposits: Rs. ")
            .append(totalDeposits)
            .append(System.lineSeparator());

        report.append("Total Withdrawals: Rs. ")
            .append(totalWithdrawals)
            .append(System.lineSeparator());

        report.append("Net Change: Rs. ")
            .append(netChange)
            .append(System.lineSeparator());

        report.append(System.lineSeparator());
        report.append(details);

        Path parent = reportFile.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(
            reportFile,
            report.toString(),
            StandardCharsets.UTF_8
        );
    }
}

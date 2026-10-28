# MiniBank — Java OOP Project

**CHARUSAT | Semester 3 | CEUE203 — Object-Oriented Programming**

**Student ID:** 25CE029

## Project Overview

MiniBank is a console-based banking application developed using Java. It demonstrates Object-Oriented Programming concepts and integrates the features developed across Practicals 1–14.

## Features

- Savings, Current, and Fixed Deposit accounts
- Account creation, deposits, withdrawals, and transfers
- Account validation and custom exceptions
- Inheritance, interfaces, polymorphism, and abstraction
- Java annotations and reflection-based validation
- Generics and repository-based account management
- Multithreading, thread pools, and concurrent transactions
- Producer-consumer synchronization
- Deadlock detection and prevention demonstrations
- Serialization and persistent account storage
- Transaction logging and report generation
- JUnit 5 automated tests
- Runnable JAR application

## Requirements

- Java JDK 17 or newer
- Terminal or Visual Studio Code
- JUnit 5 Console Standalone JAR for running automated tests

## Run the Application

From the project directory:

```powershell
java -jar MiniBank.jar
```

Alternatively, compile the Java source files:

```powershell
javac -d out MiniBank.java RepositoryTest.java model\*.java model\annotation\*.java util\*.java service\*.java exception\*.java
java -cp out MiniBank
```

## Run JUnit 5 Tests

Place the JUnit Platform Console Standalone JAR in the `lib` folder.

Compile the tests:

```powershell
javac -cp ".;lib\junit-platform-console-standalone-1.13.4.jar" -d out tests\BankServiceTest.java model\*.java model\annotation\*.java util\*.java service\*.java exception\*.java
```

Run the tests:

```powershell
java -jar lib\junit-platform-console-standalone-1.13.4.jar execute --class-path out --select-class BankServiceTest
```

### Test Results

All three JUnit 5 tests passed:

1. Deposit increases the account balance.
2. Withdrawal beyond the allowed limit throws an exception.
3. Concurrent deposits maintain the correct final balance.

**Result: 3 tests successful, 0 tests failed.**

## Data Persistence

MiniBank saves account data to `data/accounts.dat` and restores saved accounts when the application restarts. Local data files are excluded from Git using `.gitignore`.

## Project Structure

```text
src/
├── MiniBank.java
├── MiniBank.jar
├── RepositoryTest.java
├── model/
│   └── annotation/
├── service/
├── util/
├── exception/
├── tests/
│   └── BankServiceTest.java
├── .gitignore
└── README.md
```

## Course

**CEUE203 — Object-Oriented Programming**

MiniBank project covering Practicals 1–14.
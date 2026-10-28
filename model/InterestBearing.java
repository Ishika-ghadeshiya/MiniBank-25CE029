
package model;

public interface InterestBearing {

    double interestRate();

    long getBalance();

    default double yearlyInterest() {
        return Math.max(0, getBalance()) * interestRate() / 100.0;
    }
}

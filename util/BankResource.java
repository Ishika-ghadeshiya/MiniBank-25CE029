
package util;

public class BankResource implements AutoCloseable {

    public BankResource() {
        System.out.println("Bank resource opened.");
    }

    public void process() {
        System.out.println("Bank resource processing completed.");
    }

    @Override
    public void close() {
        System.out.println("Bank resource closed.");
    }
}

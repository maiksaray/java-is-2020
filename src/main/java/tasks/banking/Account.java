package tasks.banking;

public class Account {

    private final Person person;
    private final Tariff tariff;
    private double balance;

    public Account(Person person, Tariff tariff) {
        this.person = person;
        this.tariff = tariff;
    }

    public Person getPerson() {
        return person;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void replenish(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        var newBalance = balance - amount;
        if (newBalance > tariff.getAllowedOverdraft()) {
            balance -= amount;
        } else {
            throw new IllegalStateException(String.format("Cannot withdraw over overdraft! current balance: %.2f, amount: %.2f", balance, amount));
        }
    }

    public double getBalance() {
        return balance;
    }
}

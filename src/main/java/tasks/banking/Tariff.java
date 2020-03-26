package tasks.banking;

public class Tariff {
    private final String name;
    private final double interestRate;
    private final double allowedOverdraft;

    public Tariff(String name, double interestRate, double allowedOverdraft) {
        this.name = name;
        this.interestRate = interestRate;
        this.allowedOverdraft = allowedOverdraft;
    }

    public Tariff(String name, double interestRate) {
        this.name = name;
        this.interestRate = interestRate;
        this.allowedOverdraft = Double.MIN_VALUE;
    }

    public String getName() {
        return name;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getAllowedOverdraft() {
        return allowedOverdraft;
    }
}

package tasks.banking;

import tasks.banking.util.ConsoleAppender;
import tasks.banking.util.FileAppender;
import tasks.banking.util.MyLogger;
import tasks.banking.util.ParameterLock;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Bank {
    private final String logPath;
    private Set<Account> clients;
    private final Set<Tariff> defaultTariffs;
    //    private final Logger logger;
    private final MyLogger logger;

    public Bank() {
        //Hmm, maybe put this somwhere else...
        defaultTariffs = new HashSet<>() {{
            new Tariff("Regular", 0.03);
            new Tariff("VIP", 0.05);
        }};

        clients = ConcurrentHashMap.newKeySet(); //?????????

//      Switched to custom logs, just for showoff
//        logger = Logger.getLogger("Bank");
        logger = new MyLogger();
        logger.addAppender(new ConsoleAppender());
        logPath = "some/path/to/bank.log";  //TODO:move to config, or well, create these externally
        try {
            logger.addAppender(new FileAppender(logPath));
        } catch (IOException e) {
            logger.severe(String.format("Could not instance File Logging to %s, %s", logPath, e.getMessage()));
        }

        logger.info("Initialized bank");
    }

    private Tariff getTariff(String name) {
        return defaultTariffs.stream()
                .filter(t -> t.getName().equals(name))
                .findFirst().orElseThrow();
    }

    public void addClient(Person person, Tariff tariff) {
        logger.info(String.format("Adding new Client %s %s with tariff %s",
                person.getFirstName(),
                person.getLastName(),
                tariff.getName()));
        if (clients.stream().anyMatch(c -> c.getPerson() == person)) {
            logger.warning(String.format("Account for client %s %s already exists!",
                    person.getFirstName(),
                    person.getLastName()));
            throw new IllegalArgumentException("This person is already a bank client");
        }
        clients.add(new Account(person, tariff));
        logger.info(String.format("Client %s %s successfully added",
                person.getFirstName(),
                person.getLastName()));
    }

    public void addClient(Person person, String tariffName) {
        logger.info(String.format("Adding new Client %s %s with tariff %s",
                person.getFirstName(),
                person.getLastName(),
                tariffName));
        if (clients.stream().anyMatch(c -> c.getPerson() == person)) {
            logger.warning(String.format("Account for client %s %s already exists!",
                    person.getFirstName(),
                    person.getLastName()));
            throw new IllegalArgumentException("This person is already a bank client");
        }
        clients.add(new Account(person, getTariff(tariffName)));
        logger.info(String.format("Client %s %s successfully added",
                person.getFirstName(),
                person.getLastName()));
    }

    public void replenish(Person person, double amount) {
        logger.info(String.format("Adding %.2f to Client %s %s 's account",
                amount,
                person.getFirstName(),
                person.getLastName()));
        var account = getAccount(person);
        logger.info(String.format("Account's tariff is %s", account.getTariff().getName()));
        var lock = ParameterLock.getCanonicalParameterLock(account);
        lock.lock();
        try {
            account.replenish(amount);
            logger.info(String.format("successfully Added %.2f to Client %s %s 's account",
                    amount,
                    person.getFirstName(),
                    person.getLastName()));
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    private Account getAccount(Person person) {
        return clients.stream()
                .filter(a -> a.getPerson().equals(person))
                .findFirst().orElseThrow();
    }

    public void withdraw(Person person, double amount) {
        logger.info(String.format("Taking %.2f from Client %s %s 's account",
                amount,
                person.getFirstName(),
                person.getLastName()));
        var account = getAccount(person);
        logger.info(String.format("Account's tariff is %s", account.getTariff().getName()));
        var lock = ParameterLock.getCanonicalParameterLock(account);
        lock.lock();
        try {
            account.withdraw(amount);
            logger.info(String.format("successfully taken %.2f from Client %s %s 's account",
                    amount,
                    person.getFirstName(),
                    person.getLastName()));
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void accrue(Person person) {
        logger.info(String.format("Adding interest to Client %s %s 's account",
                person.getFirstName(),
                person.getLastName()));
        var account = getAccount(person);
        var currentBalance = account.getBalance();
        var tariff = account.getTariff();
        logger.info(String.format("Tariff is %s and current balance is %.2f", tariff.getName(), currentBalance));
        var lock = ParameterLock.getCanonicalParameterLock(account);
        lock.lock();
        try {
            var newBalance = currentBalance * tariff.getInterestRate();
            account.replenish(newBalance);
            logger.info(String.format("New balance is %.2f", newBalance));
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void transfer(Person from, Person to, double amount) {
        logger.info("Initiating transfer, from to, amount: ");
        var accountFrom = getAccount(from);
        var accountTo = getAccount(to);
        var lockFrom = ParameterLock.getCanonicalParameterLock(accountFrom);
        var lockTo = ParameterLock.getCanonicalParameterLock(accountTo);
//        Lock ordering to avoid deadlocking =)
        if (from.compareTo(to) < 0) {
            lockFrom.lock();
            lockTo.lock();
        } else {
            lockTo.lock();
            lockFrom.lock();
        }
        try {
            accountFrom.withdraw(amount);
            accountTo.replenish(amount);
            logger.info("transfer successfull");
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            lockFrom.unlock();
            lockTo.unlock();
        }
    }
}

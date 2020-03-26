package tasks.banking.util;

public class ConsoleAppender extends Appender {
    @Override
    public void append(String logLine) {
        System.out.println(logLine);
    }
}

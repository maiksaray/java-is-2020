package tasks.banking.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MyLogger{
    private List<Appender> appenders;

    private String constructLogLine(String level, String log) {
        var builder = new StringBuilder();
        builder.append(level);
        builder.append(log);
        return builder.toString();
    }

    public MyLogger() {
        appenders = new ArrayList<>();
    }

    public void addAppender(Appender appender) {
        appenders.add(appender);
    }

    public void info(String logLine) {
        for (var appender : appenders) {
            appender.append(constructLogLine("[INFO]", logLine));
        }
    }

    public void warning(String logLine) {
        for (var appender : appenders) {
            appender.append(constructLogLine("[WARN]", logLine));
        }
    }

    public void severe(String logLine) {
        for (var appender : appenders) {
            appender.append(constructLogLine("[SEVERE]", logLine));
        }
    }
}

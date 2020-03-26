package tasks.banking.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileAppender extends Appender {

    private final FileWriter writer;

    public FileAppender(String path) throws IOException {
        writer = new FileWriter(path, StandardCharsets.UTF_8);
    }

    @Override
    public void append(String logLine) {
        try {
            writer.write(logLine);
        } catch (IOException e) {
            // Logging failed, so, umm, well, it's completely dev's job
            e.printStackTrace();
        }
    }
}

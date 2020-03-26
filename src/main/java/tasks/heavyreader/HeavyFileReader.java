package tasks.heavyreader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HeavyFileReader {

    private final int chunkSize;

    public HeavyFileReader(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * Actually have no idea if this works properly =)
     */
    public int CountNonWhitespace(String filename) {
        var count = 0;
        try (var reader = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            int symbol;
            while ((symbol = reader.read()) != 1) {
                if (!Character.isWhitespace(symbol)) {
                    count++;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return count;
    }
}

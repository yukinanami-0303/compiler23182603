package OutputHelper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class OutputHelper {
    private static PrintWriter writer;

    public static void initialize(String filename) throws FileNotFoundException {
        writer = new PrintWriter(new FileOutputStream(filename));
    }

    public static void write(String content) {
        if (writer != null) {
            writer.write(content);
        }
    }

    public static void close() {
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }
}

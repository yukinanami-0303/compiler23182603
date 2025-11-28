package frontend;
import OutputHelper.OutputHelper;

import java.io.*;

public class Token {
    private final String type;
    private final String value;
    private final int lineNumber;

    public Token(String type, String value, int lineNumber) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLineNumber() {
        return lineNumber;
    }
    public void formatOutput() throws IOException {
        OutputHelper.write(type + " " + value + "\n");
    }

    @Override
    public String toString() {
        return type + " " + value;
    }
}

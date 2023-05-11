

package jclang;

import org.jetbrains.annotations.NotNull;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private TestUtils() {}

    @NotNull
    public static String loadFileContents(@NotNull String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = reader.readLine()) != null) {
            sb.append(s).append(LINE_SEPARATOR);
        }
        reader.close();
        return sb.toString();
    }

    @NotNull
    public static File createTempFileWithContents(@NotNull String contents) {
        try {
            File dummy = File.createTempFile("jclang", ".h");
            PrintWriter out = new PrintWriter(dummy);
            out.print(contents);
            out.close();
            return dummy;
        }
        catch (Exception e) {
            throw rethrow(e);
        }
    }

    @NotNull
    public static RuntimeException rethrow(@NotNull Exception e) {
        throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }

    public static void createOrCompare(@NotNull String actual, @NotNull String expectedFileName) {
        try {
            String expected = loadFileContents(expectedFileName);
            Assertions.assertEquals(expected, actual);
        }
        catch (IOException e) {
            try {
                PrintWriter out = new PrintWriter(new File(expectedFileName));
                out.print(actual);
                out.close();
                Assertions.assertEquals("Expected file wasn't found, it will be created", "", actual);
            }
            catch (IOException ee) {
                ee.printStackTrace();
                Assertions.fail(ee.getMessage());
            }
        }
    }
}

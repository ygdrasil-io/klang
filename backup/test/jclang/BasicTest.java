
package jclang;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jclang.Diagnostic.DisplayOptions.*;
import static jclang.Diagnostic.Severity;
import static jclang.Diagnostic.Severity.*;
import static jclang.TestUtils.createOrCompare;
import static org.junit.jupiter.api.Assertions.*;

public class BasicTest extends ClangTest {
    @NotNull
    private String getDir() {
        return getTestDataDir() + "/basic/";
    }

    @NotNull
    private static String lettersOnly(@NotNull String s) {
        return s.replaceAll("[^A-Za-z]+", "").toLowerCase();
    }

    @Test
    public void testVersion() {
        assertEquals("clang version 9.0.1".trim(), Clang.INSTANCE.getVersion().trim());
    }

    @Test
    public void testCreateIndex() {
        boolean[] booleans = {false, true};
        for (boolean excludeDeclarationsFromPCH : booleans) {
            for (boolean displayDiagnostics : booleans) {
                Assertions.assertNotNull(Clang.INSTANCE.createIndex(excludeDeclarationsFromPCH, displayDiagnostics));
            }
        }
    }

    @Test
    public void testParseTranslationUnit() {
        Index index = Clang.INSTANCE.createIndex(false, false);
        index.parseTranslationUnit(TestUtils.createTempFileWithContents("").getAbsolutePath(), new String[]{});
    }

    @Test
    public void testTranslationException() {
        Index index = Clang.INSTANCE.createIndex(false, false);
        try {
            index.parseTranslationUnit(null, new String[]{});
        } catch (TranslationException e) {
            return;
        }
        Assertions.fail("TranslationException expected");
    }

    @Test
    public void testDiagnosticDisplayOptions() {
        Index index = Clang.INSTANCE.createIndex(false, false);
        TranslationUnit unit = index.parseTranslationUnit(getDir() + "diagnosticDisplayOptions.h", new String[]{});
        List<Diagnostic> diagnostics = unit.getDiagnostics();
        Assertions.assertEquals(1, diagnostics.size());
        Diagnostic diagnostic = diagnostics.iterator().next();
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        out.println(diagnostic.format(DISPLAY_SOURCE_LOCATION));
        out.println(diagnostic.format(DISPLAY_SOURCE_LOCATION, DISPLAY_COLUMN));
        out.println(diagnostic.format(DISPLAY_SOURCE_LOCATION, DISPLAY_SOURCE_RANGES));
        out.println(diagnostic.format(DISPLAY_OPTION));
        out.println(diagnostic.format(DISPLAY_CATEGORY_ID));
        out.println(diagnostic.format(DISPLAY_CATEGORY_NAME));
        out.close();
        createOrCompare(sw.toString(), getDir() + "diagnosticDisplayOptions.txt");
    }

    @Test
    public void testDiagnosticSeverity() {
        Index index = Clang.INSTANCE.createIndex(false, false);
        TranslationUnit unit = index.parseTranslationUnit(getDir() + "diagnosticSeverity.h", new String[]{});
        List<Diagnostic> diagnostics = unit.getDiagnostics();
        List<Severity> actual = new ArrayList<Severity>(diagnostics.size());
        for (Diagnostic diagnostic : diagnostics) {
            actual.add(diagnostic.getSeverity());
        }
        Assertions.assertEquals(Arrays.asList(ERROR, WARNING, FATAL), actual);
    }

    @Test
    public void testCursorKinds() {
        // For some reason, CXCursorKind spelling is not always equal to the enum constant name, so we check spellings with a saved list
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);

        for (CursorKind kind : CursorKind.values()) {
            out.print(kind.toNative());
            String spelling = kind.getSpelling();
            if (!lettersOnly(spelling).equals(lettersOnly(kind.toString()))) {
                out.print(" " + kind);
            }
            out.println(" " + spelling);
        }
        out.close();

        createOrCompare(sw.toString(), getDir() + "cursorKinds.txt");
    }

    @Test
    public void testTypeKinds() {
        StringWriter swNative = new StringWriter();
        StringWriter swEnum = new StringWriter();
        PrintWriter outNative = new PrintWriter(swNative);
        PrintWriter outEnum = new PrintWriter(swEnum);
        for (TypeKind kind : TypeKind.values()) {
            outNative.println(lettersOnly(kind.getSpelling()));
            outEnum.println(lettersOnly(kind.toString()));
        }
        outNative.close();
        outEnum.close();
        Assertions.assertEquals(swNative.toString(), swEnum.toString());
    }
}

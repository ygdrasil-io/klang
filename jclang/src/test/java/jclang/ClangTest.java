

package jclang;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;

import java.io.File;

public abstract class ClangTest {

    @AfterEach
    protected void tearDown() {
        Clang.INSTANCE.disposeAll();
    }

    @NotNull
    protected static File getTestDataDir() {
        return new File("testData/");
    }
}



package jclang;

import com.sun.jna.PointerType;
import org.jetbrains.annotations.NotNull;
import jclang.structs.CXString;

import java.io.File;

public class CXFile extends PointerType {
    @NotNull
    public File toFile() {
        CXString.ByValue name = LibClang.I.getFileName(this);
        NativePool.I.record(name);
        return new File(LibClang.I.getCString(name));
    }
}

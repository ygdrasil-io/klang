

package jclang;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.jetbrains.annotations.NotNull;
import jclang.structs.CXIdxLoc;

import java.io.File;

public class IndexLocation {
    private final CXIdxLoc.ByValue location;

    private boolean isComputed;
    private File file;
    private int line;
    private int column;
    private int offset;

    public IndexLocation(@NotNull CXIdxLoc.ByValue location) {
        this.location = location;
    }

    private void retrieveLocation() {
        if (isComputed) return;
        isComputed = true;

        PointerByReference file = new PointerByReference();
        IntByReference line = new IntByReference();
        IntByReference column = new IntByReference();
        IntByReference offset = new IntByReference();
        LibClang.I.indexLoc_getFileLocation(location, null, file, line, column, offset);

        CXFile cxFile = new CXFile();
        cxFile.setPointer(file.getValue());
        this.file = cxFile.toFile();
        this.line = line.getValue();
        this.column = column.getValue();
        this.offset = offset.getValue();
    }

    // TODO: probably is null for unsaved files, investigate
    @NotNull
    public File getFile() {
        retrieveLocation();
        return file;
    }

    public int getLine() {
        retrieveLocation();
        return line;
    }

    public int getColumn() {
        retrieveLocation();
        return column;
    }

    public int getOffset() {
        retrieveLocation();
        return offset;
    }

    @NotNull
    public String toString() {
        return getFile() + "[" + getOffset() + "]:" + getLine() + ":" + getColumn();
    }
}

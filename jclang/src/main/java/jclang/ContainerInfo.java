

package jclang;

import org.jetbrains.annotations.NotNull;
import jclang.structs.CXIdxContainerInfo;

public class ContainerInfo {
    private final Cursor cursor;

    public ContainerInfo(@NotNull CXIdxContainerInfo info) {
        this.cursor = new Cursor(info.cursor);
    }

    @NotNull
    public Cursor getCursor() {
        return cursor;
    }
}

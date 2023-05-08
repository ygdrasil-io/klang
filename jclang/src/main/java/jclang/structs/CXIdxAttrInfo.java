

package jclang.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Structure.FieldOrder({"kind", "cursor", "loc"})
public class CXIdxAttrInfo extends Structure {
    public int kind;
    public CXCursor.ByValue cursor;
    public CXIdxLoc.ByValue loc;

    public CXIdxAttrInfo() {
        super();
    }

    public CXIdxAttrInfo(@NotNull Pointer pointer) {
        super(pointer);
        read();
    }

}

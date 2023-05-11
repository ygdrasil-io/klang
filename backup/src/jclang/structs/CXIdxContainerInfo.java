

package jclang.structs;

import com.sun.jna.Structure;

@SuppressWarnings("unused")
@Structure.FieldOrder({"cursor"})
public class CXIdxContainerInfo extends Structure {
    public CXCursor.ByValue cursor;

    public static class ByReference extends CXIdxContainerInfo implements Structure.ByReference {}
}

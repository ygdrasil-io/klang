
package klang.jvm.binding;

import com.sun.jna.Structure;

@SuppressWarnings("unused")
@Structure.FieldOrder({"cursor"})
public class CXIdxContainerInfo extends Structure {
    public CXCursor.CXCursorByValue cursor;

    public static class ByReference extends CXIdxContainerInfo implements Structure.ByReference {}
}

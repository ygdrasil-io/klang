

package jclang.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@SuppressWarnings("unused")
@Structure.FieldOrder({"data", "privateFlags"})
public class CXString extends Structure {
    public Pointer data;
    public int privateFlags;

    public static class ByValue extends CXString implements Structure.ByValue {}
}



package jclang.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@SuppressWarnings("unused")
@Structure.FieldOrder({"kind", "xdata", "data"})
public class CXCursor extends Structure {
    public int kind;
    public int xdata;
    public Pointer[] data = new Pointer[3];

    public static class ByValue extends CXCursor implements Structure.ByValue {}
}

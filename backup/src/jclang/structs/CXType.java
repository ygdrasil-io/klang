

package jclang.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@SuppressWarnings("unused")
@Structure.FieldOrder({"kind", "data"})
public class CXType extends Structure {
    public int kind;
    public Pointer[] data = new Pointer[2];

    public static class ByValue extends CXType implements Structure.ByValue {}
}

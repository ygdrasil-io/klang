
package klang.jvm.binding;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@SuppressWarnings("unused")
@Structure.FieldOrder({"ptr_data", "int_data"})
public class CXIdxLoc extends Structure {
    public Pointer[] ptr_data = new Pointer[2];
    public int int_data;

    public static class ByValue extends CXIdxLoc implements Structure.ByValue {}
}

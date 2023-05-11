

package jclang.structs;

import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@Structure.FieldOrder({"kind", "templateKind", "lang", "name", "USR", "cursor", "attributes", "numAttributes"})
public class CXIdxEntityInfo extends Structure {
    public int kind;
    public int templateKind;
    public int lang;
    public String name;
    public String USR;
    public CXCursor.ByValue cursor;
    @Nullable
    public PointerByReference /* CXIdxAttrInfo */ attributes;
    public int numAttributes;

    public static class ByReference extends CXIdxEntityInfo implements Structure.ByReference {}
}

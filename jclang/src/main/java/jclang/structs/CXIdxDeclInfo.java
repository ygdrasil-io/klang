

package jclang.structs;

import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@Structure.FieldOrder({"entityInfo", "cursor", "loc", "semanticContainer", "lexicalContainer", "isRedeclaration",
        "isDefinition", "isContainer", "declAsContainer", "isImplicit", "attributes", "numAttributes"})
public class CXIdxDeclInfo extends Structure {
    public CXIdxEntityInfo.ByReference entityInfo;
    public CXCursor.ByValue cursor;
    public CXIdxLoc.ByValue loc;
    public CXIdxContainerInfo.ByReference semanticContainer;
    public CXIdxContainerInfo.ByReference lexicalContainer;
    public boolean isRedeclaration;
    public boolean isDefinition;
    public boolean isContainer;
    public CXIdxContainerInfo.ByReference declAsContainer;
    public boolean isImplicit;
    @Nullable
    public PointerByReference /* CXIdxAttrInfo */ attributes;
    public int numAttributes;

    public static class ByReference extends CXIdxDeclInfo implements Structure.ByReference {}
}

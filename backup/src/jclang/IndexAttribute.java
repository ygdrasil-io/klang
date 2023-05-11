

package jclang;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jclang.structs.CXIdxAttrInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndexAttribute {
    public enum Kind {
        UNEXPOSED,
        IB_ACTION,
        IB_OUTLET,
        IB_OUTLET_COLLECTION
    }

    private final Kind kind;
    private final Cursor cursor;
    private final IndexLocation location;

    public IndexAttribute(@NotNull CXIdxAttrInfo info) {
        this.kind = Kind.values()[info.kind];
        this.cursor = new Cursor(info.cursor);
        this.location = new IndexLocation(info.loc);
    }

    @NotNull
    /* package */ static List<IndexAttribute> createFromNative(@Nullable PointerByReference attributes, int numAttributes) {
        if (attributes == null || numAttributes == 0) {
            return Collections.emptyList();
        }
        CXIdxAttrInfo attrInfo = new CXIdxAttrInfo(attributes.getValue());
        List<IndexAttribute> result = new ArrayList<IndexAttribute>(numAttributes);
        for (int i = 0; i < numAttributes; i++) {
            // TODO: ugly hack, figure out how to make toArray() stuff work here
            CXIdxAttrInfo info = new CXIdxAttrInfo(attributes.getPointer().getPointer((long) i * attrInfo.size() / Native.POINTER_SIZE));
            result.add(new IndexAttribute(info));
        }
        return result;
    }

    @NotNull
    public Kind getKind() {
        return kind;
    }

    @NotNull
    public Cursor getCursor() {
        return cursor;
    }

    @NotNull
    public IndexLocation getLocation() {
        return location;
    }
}

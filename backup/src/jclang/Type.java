

package jclang;

import org.jetbrains.annotations.NotNull;
import jclang.structs.CXType;

public class Type {
    private final CXType.ByValue type;

    public Type(@NotNull CXType.ByValue type) {
        this.type = type;
    }

    @NotNull
    public TypeKind getKind() {
        return TypeKind.fromNative(type.kind);
    }
}

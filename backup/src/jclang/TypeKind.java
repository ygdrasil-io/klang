

package jclang;

import org.jetbrains.annotations.NotNull;
import jclang.structs.CXString;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public enum TypeKind {
    INVALID,
    UNEXPOSED,
    VOID,
    BOOL,
    CHAR_U,
    UCHAR,
    CHAR16,
    CHAR32,
    USHORT,
    UINT,
    ULONG,
    ULONG_LONG,
    UINT128,
    CHAR_S,
    SCHAR,
    WCHAR,
    SHORT,
    INT,
    LONG,
    LONG_LONG,
    INT128,
    FLOAT,
    DOUBLE,
    LONG_DOUBLE,
    NULL_PTR,
    OVERLOAD,
    DEPENDENT,
    OBJC_ID,
    OBJC_CLASS,
    OBJC_SEL,

    COMPLEX,
    POINTER,
    BLOCK_POINTER,
    LVALUE_REFERENCE,
    RVALUE_REFERENCE,
    RECORD,
    ENUM,
    TYPEDEF,
    OBJC_INTERFACE,
    OBJC_OBJECT_POINTER,
    FUNCTION_NO_PROTO,
    FUNCTION_PROTO,
    CONSTANT_ARRAY,
    VECTOR,
    Incompletearray,
    VariableArray ,
    DependentSizedArray ,
    MemberPointer ,
    Auto ,

    /**
     * Represents a type that was referred to using an elaborated type keyword.
     *
     * E.g., struct S, or via a qualified name, e.g., N::M::type, or both.
     */
    Elaborated ,

    /* OpenCL PipeType. */
    Pipe ,

    /* OpenCL builtin types. */
    OCLImage1dRO ,
    OCLImage1dArrayRO ,
    OCLImage1dBufferRO ,
    OCLImage2dRO,
    OCLImage2dArrayRO,
    OCLImage2dDepthRO,
    OCLImage2dArrayDepthRO,
    OCLImage2dMSAARO,
    OCLImage2dArrayMSAARO,
    OCLImage2dMSAADepthRO,
    OCLImage2dArrayMSAADepthRO,
    OCLImage3dRO,
    OCLImage1dWO,
    OCLImage1dArrayWO,
    OCLImage1dBufferWO,
    OCLImage2dWO,
    OCLImage2dArrayWO,
    OCLImage2dDepthWO,
    OCLImage2dArrayDepthWO,
    OCLImage2dMSAAWO,
    OCLImage2dArrayMSAAWO,
    OCLImage2dMSAADepthWO,
    OCLImage2dArrayMSAADepthWO,
    OCLImage3dWO,
    OCLImage1dRW,
    OCLImage1dArrayRW,
    OCLImage1dBufferRW,
    OCLImage2dRW,
    OCLImage2dArrayRW,
    OCLImage2dDepthRW,
    OCLImage2dArrayDepthRW,
    OCLImage2dMSAARW,
    OCLImage2dArrayMSAARW,
    OCLImage2dMSAADepthRW,
    OCLImage2dArrayMSAADepthRW,
    OCLImage3dRW,
    OCLSampler,
    OCLEvent,
    OCLQueue,
    OCLReserveID,

    ObjCObject,
    ObjCTypeParam,
    Attributed,

    OCLIntelSubgroupAVCMcePayload,
    OCLIntelSubgroupAVCImePayload,
    OCLIntelSubgroupAVCRefPayload,
    OCLIntelSubgroupAVCSicPayload,
    OCLIntelSubgroupAVCMceResult,
    OCLIntelSubgroupAVCImeResult,
    OCLIntelSubgroupAVCRefResult,
    OCLIntelSubgroupAVCSicResult,
    OCLIntelSubgroupAVCImeResultSingleRefStreamout,
    OCLIntelSubgroupAVCImeResultDualRefStreamout,
    OCLIntelSubgroupAVCImeSingleRefStreamin,

    OCLIntelSubgroupAVCImeDualRefStreamin,

    ExtVector;

    private static final Map<Integer, TypeKind> FROM_NATIVE = new HashMap<Integer, TypeKind>();
    private static final Map<TypeKind, Integer> TO_NATIVE = new EnumMap<TypeKind, Integer>(TypeKind.class);

    static {
        // The code below depends on the actual CXCursorKind enum values
        int nativeValue = 0;
        for (TypeKind enumValue : values()) {
            if (enumValue == COMPLEX) {
                nativeValue = 100;
            }
            FROM_NATIVE.put(nativeValue, enumValue);
            TO_NATIVE.put(enumValue, nativeValue);
            nativeValue++;
        }
    }

    @NotNull
    /* package */ static TypeKind fromNative(int kind) {
        TypeKind result = FROM_NATIVE.get(kind);
        if (result == null) {
            throw new IllegalStateException("Unknown CXTypeKind value: " + kind + ". Probably an incompatible libclang version");
        }
        return result;
    }

    /* package */ int toNative() {
        Integer result = TO_NATIVE.get(this);
        if (result == null) {
            throw new IllegalStateException("No corresponding CXTypeKind value: " + this + ". Probably an incompatible libclang version");
        }
        return result;
    }

    @NotNull
    public String getSpelling() {
        CXString.ByValue spelling = LibClang.I.getTypeKindSpelling(toNative());
        NativePool.I.record(spelling);
        return LibClang.I.getCString(spelling);
    }
}

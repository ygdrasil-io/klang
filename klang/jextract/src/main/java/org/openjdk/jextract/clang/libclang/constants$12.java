/*
 *  Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This code is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License version 2 only, as
 *  published by the Free Software Foundation.  Oracle designates this
 *  particular file as subject to the "Classpath" exception as provided
 *  by Oracle in the LICENSE file that accompanied this code.
 *
 *  This code is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  version 2 for more details (a copy is included in the LICENSE file that
 *  accompanied this code).
 *
 *  You should have received a copy of the GNU General Public License version
 *  2 along with this work; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *   Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 *  or visit www.oracle.com if you need additional information or have any
 *  questions.
 */

// Generated by jextract

package org.openjdk.jextract.clang.libclang;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;
final class constants$12 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$12() {}
    static final FunctionDescriptor clang_getNumElements$FUNC = FunctionDescriptor.of(Constants$root.C_LONG_LONG$LAYOUT,
        MemoryLayout.structLayout(
            Constants$root.C_INT$LAYOUT.withName("kind"),
            MemoryLayout.paddingLayout(4),
            MemoryLayout.sequenceLayout(2, Constants$root.C_POINTER$LAYOUT).withName("data")
        )
    );
    static final MethodHandle clang_getNumElements$MH = RuntimeHelper.downcallHandle(
        "clang_getNumElements",
        constants$12.clang_getNumElements$FUNC
    );
    static final FunctionDescriptor clang_getArrayElementType$FUNC = FunctionDescriptor.of(MemoryLayout.structLayout(
        Constants$root.C_INT$LAYOUT.withName("kind"),
        MemoryLayout.paddingLayout(4),
        MemoryLayout.sequenceLayout(2, Constants$root.C_POINTER$LAYOUT).withName("data")
    ),
        MemoryLayout.structLayout(
            Constants$root.C_INT$LAYOUT.withName("kind"),
            MemoryLayout.paddingLayout(4),
            MemoryLayout.sequenceLayout(2, Constants$root.C_POINTER$LAYOUT).withName("data")
        )
    );
    static final MethodHandle clang_getArrayElementType$MH = RuntimeHelper.downcallHandle(
        "clang_getArrayElementType",
        constants$12.clang_getArrayElementType$FUNC
    );
    static final FunctionDescriptor clang_getArraySize$FUNC = FunctionDescriptor.of(Constants$root.C_LONG_LONG$LAYOUT,
        MemoryLayout.structLayout(
            Constants$root.C_INT$LAYOUT.withName("kind"),
            MemoryLayout.paddingLayout(4),
            MemoryLayout.sequenceLayout(2, Constants$root.C_POINTER$LAYOUT).withName("data")
        )
    );
    static final MethodHandle clang_getArraySize$MH = RuntimeHelper.downcallHandle(
        "clang_getArraySize",
        constants$12.clang_getArraySize$FUNC
    );
    static final FunctionDescriptor clang_Type_getSizeOf$FUNC = FunctionDescriptor.of(Constants$root.C_LONG_LONG$LAYOUT,
        MemoryLayout.structLayout(
            Constants$root.C_INT$LAYOUT.withName("kind"),
            MemoryLayout.paddingLayout(4),
            MemoryLayout.sequenceLayout(2, Constants$root.C_POINTER$LAYOUT).withName("data")
        )
    );
    static final MethodHandle clang_Type_getSizeOf$MH = RuntimeHelper.downcallHandle(
        "clang_Type_getSizeOf",
        constants$12.clang_Type_getSizeOf$FUNC
    );
    static final FunctionDescriptor clang_Type_getAlignOf$FUNC = FunctionDescriptor.of(Constants$root.C_LONG_LONG$LAYOUT,
            MemoryLayout.structLayout(
                    Constants$root.C_INT$LAYOUT.withName("kind"),
                    MemoryLayout.paddingLayout(4),
                    MemoryLayout.sequenceLayout(2, Constants$root.C_POINTER$LAYOUT).withName("data")
            )
    );
    static final MethodHandle clang_Type_getAlignOf$MH = RuntimeHelper.downcallHandle(
            "clang_Type_getAlignOf",
            constants$12.clang_Type_getAlignOf$FUNC
    );
    static final FunctionDescriptor clang_Type_getOffsetOf$FUNC = FunctionDescriptor.of(Constants$root.C_LONG_LONG$LAYOUT,
        MemoryLayout.structLayout(
            Constants$root.C_INT$LAYOUT.withName("kind"),
            MemoryLayout.paddingLayout(4),
            MemoryLayout.sequenceLayout(2, Constants$root.C_POINTER$LAYOUT).withName("data")
        ),
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle clang_Type_getOffsetOf$MH = RuntimeHelper.downcallHandle(
        "clang_Type_getOffsetOf",
        constants$12.clang_Type_getOffsetOf$FUNC
    );
    static final FunctionDescriptor clang_Cursor_isAnonymous$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        MemoryLayout.structLayout(
            Constants$root.C_INT$LAYOUT.withName("kind"),
            Constants$root.C_INT$LAYOUT.withName("xdata"),
            MemoryLayout.sequenceLayout(3, Constants$root.C_POINTER$LAYOUT).withName("data")
        )
    );
    static final MethodHandle clang_Cursor_isAnonymous$MH = RuntimeHelper.downcallHandle(
        "clang_Cursor_isAnonymous",
        constants$12.clang_Cursor_isAnonymous$FUNC
    );
}


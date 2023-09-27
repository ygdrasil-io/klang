/*
 * Copyright 2013 Alexander Udalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package klang.jvm.binding

import com.sun.jna.Structure
import com.sun.jna.ptr.PointerByReference
import klang.jvm.binding.CXCursor.CXCursorByValue

@Suppress("unused")
@Structure.FieldOrder("entityInfo", "cursor", "loc", "semanticContainer", "lexicalContainer", "isRedeclaration", "isDefinition", "isContainer", "declAsContainer", "isImplicit", "attributes", "numAttributes")
open class CXIdxDeclInfo : Structure() {
	@JvmField
    var entityInfo: CXIdxEntityInfo.ByReference = CXIdxEntityInfo.ByReference()
	@JvmField
    var cursor: CXCursorByValue = CXCursorByValue()
	@JvmField
    var loc: CXIdxLoc.ByValue = CXIdxLoc.ByValue()
	@JvmField
    var semanticContainer: CXIdxContainerInfoReference = CXIdxContainerInfoReference()
	@JvmField
    var lexicalContainer: CXIdxContainerInfoReference = CXIdxContainerInfoReference()
	@JvmField
    var isRedeclaration = false
	@JvmField
    var isDefinition = false
	@JvmField
    var isContainer = false
	@JvmField
    var declAsContainer: CXIdxContainerInfoReference = CXIdxContainerInfoReference()
	@JvmField
    var isImplicit = false
	@JvmField
    var  /* CXIdxAttrInfo */attributes: PointerByReference = PointerByReference()
	@JvmField
    var numAttributes = 0

    class ByReference : CXIdxDeclInfo(), Structure.ByReference
}

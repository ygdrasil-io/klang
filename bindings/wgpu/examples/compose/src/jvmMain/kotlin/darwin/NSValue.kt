package darwin /*
 * Copyright (c) 2002-2009 David Kocher. All rights reserved.
 *
 * http://cyberduck.ch/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Bug fixes, suggestions and comments should be sent to:
 * dkocher@cyberduck.ch
 */

import org.rococoa.ObjCClass
import org.rococoa.cocoa.foundation.NSSize

/// <i>native declaration : /Users/dkocher/null:10</i>
abstract class NSValue : NSObject() {
	interface _Class : ObjCClass {
		open fun valueWithSize(size: NSSize?): NSValue?
	}

	/**
	 * Original signature : `BOOL isEqualToValue(NSValue*)`<br></br>
	 * *from NSValueExtensionMethods native declaration : /Users/dkocher/null:33*
	 */
	abstract fun isEqualToValue(value: NSValue?): Byte

	companion object {
		private val CLASS: _Class = org.rococoa.Rococoa.createClass("NSValue", _Class::class.java)

		fun valueWithSize(size: NSSize?): NSValue? {
			return CLASS.valueWithSize(size)
		}
	}
}

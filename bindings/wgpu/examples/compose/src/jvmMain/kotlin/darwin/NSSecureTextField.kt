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
import org.rococoa.cocoa.foundation.NSRect

abstract class NSSecureTextField : NSTextField() {
	interface _Class : ObjCClass {
		open fun alloc(): NSSecureTextField
	}

	@Override
	abstract override fun initWithFrame(frameRect: NSRect?): NSSecureTextField

	companion object {
		private val CLASS: _Class = org.rococoa.Rococoa.createClass("NSSecureTextField", _Class::class.java)

		fun textfieldWithFrame(frameRect: NSRect?): NSSecureTextField? {
			return CLASS.alloc().initWithFrame(frameRect)
		}
	}
}

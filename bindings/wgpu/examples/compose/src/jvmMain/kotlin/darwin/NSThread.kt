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

import org.rococoa.ID
import org.rococoa.ObjCClass
import org.rococoa.Selector
import org.rococoa.cocoa.foundation.NSUInteger

object NSThread : NSObject() {
	private val CLASS: _Class = org.rococoa.Rococoa.createClass("NSThread", _Class::class.java)

	fun isMainThread(): Boolean {
		return CLASS.isMainThread()
	}

	interface _Class : ObjCClass {
		/**
		 * Returns a Boolean value that indicates whether the current thread is the main thread.
		 *
		 * @return
		 */
		open fun isMainThread(): Boolean
	}

	override fun respondsToSelector(sel: Selector?): Boolean {
		TODO("Not yet implemented")
	}

	override fun performSelector(sel: Selector?): NSObject? {
		TODO("Not yet implemented")
	}

	override fun hash(): NSUInteger? {
		TODO("Not yet implemented")
	}

	override fun isEqual(anObject: ID?): Boolean {
		TODO("Not yet implemented")
	}

	override fun id(): ID {
		TODO("Not yet implemented")
	}

	override fun retain(): org.rococoa.cocoa.foundation.NSObject {
		TODO("Not yet implemented")
	}

	override fun release() {
		TODO("Not yet implemented")
	}

	override fun retainCount(): NSUInteger {
		TODO("Not yet implemented")
	}

	override fun isKindOfClass(p0: ObjCClass?): Boolean {
		TODO("Not yet implemented")
	}

	override fun isKindOfClass(p0: ID?): Boolean {
		TODO("Not yet implemented")
	}

	override fun description(): String {
		TODO("Not yet implemented")
	}
}

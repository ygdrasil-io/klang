package darwin /*
 * Copyright (c) 2002-2015 David Kocher. All rights reserved.
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
 * Bug fixes, suggestions and comments should be sent to feedback@cyberduck.ch
 */

import org.rococoa.ObjCClass
import org.rococoa.cocoa.CGFloat

abstract class NSStatusBar : NSObject() {
	interface _Class : ObjCClass {
		open fun systemStatusBar(): NSStatusBar?
	}

	/**
	 * @param length A constant that specifies whether the status item is of fixed width, or variable width.
	 * The valid constants are described in Status Bar Item Length.
	 * @return An NSStatusItem object or nil if the item could not be created.
	 */
	abstract fun statusItemWithLength(length: CGFloat?): NSStatusItem?

	/**
	 * Removes the specified status item from the receiver
	 *
	 * @param item The NSStatusItem object to remove.
	 */
	abstract fun removeStatusItem(item: NSStatusItem?)

	companion object {
		private val CLASS: _Class = org.rococoa.Rococoa.createClass("NSStatusBar", _Class::class.java)

		val NSVariableStatusItemLength: CGFloat? = CGFloat(-1.0)
		val NSSquareStatusItemLength: CGFloat? = CGFloat(-2.0)

		fun systemStatusBar(): NSStatusBar? {
			return CLASS.systemStatusBar()
		}
	}
}

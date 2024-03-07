package darwin /*
 * Copyright (c) 2002-2019 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import org.rococoa.ID
import org.rococoa.ObjCClass
import org.rococoa.cocoa.foundation.NSRect

abstract class NSPathControl : NSControl() {
	interface _Class : ObjCClass {
		open fun alloc(): NSPathControl
	}

	@Override
	abstract override fun initWithFrame(frameRect: NSRect?): NSPathControl

	abstract fun URL(): NSURL?

	abstract fun setURL(aString: NSURL?)

	abstract fun setDelegate(delegate: ID?)

	interface Delegate {
		open fun pathControl_willDisplayOpenPanel(control: NSPathControl?, panel: NSOpenPanel?)
	}

	companion object {
		private val CLASS: _Class = org.rococoa.Rococoa.createClass("NSPathControl", _Class::class.java)

		fun pathControlWithFrame(frameRect: NSRect?): NSPathControl? {
			return CLASS.alloc().initWithFrame(frameRect)
		}
	}
}

package darwin /*
 * Copyright (c) 2002-2016 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
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
 */

import org.rococoa.ObjCClass
import org.rococoa.cocoa.foundation.NSInteger

abstract class NSTitlebarAccessoryViewController : NSResponder() {
	interface _Class : ObjCClass {
		open fun alloc(): NSTitlebarAccessoryViewController
	}

	abstract fun init(): NSTitlebarAccessoryViewController

	abstract fun removeFromParentViewController()

	abstract fun setLayoutAttribute(layout: NSInteger?)

	abstract fun setAutomaticallyAdjustsSize(value: Boolean)

	abstract fun setView(view: NSView?)

	companion object {
		private val CLASS: _Class =
			org.rococoa.Rococoa.createClass("NSTitlebarAccessoryViewController", _Class::class.java)

		val NSLayoutAttributeLeft: NSInteger? = NSInteger(1)
		val NSLayoutAttributeRight: NSInteger? = NSInteger(2)
		val NSLayoutAttributeBottom: NSInteger? = NSInteger(4)

		fun create(): NSTitlebarAccessoryViewController? {
			return CLASS.alloc().init()
		}
	}
}

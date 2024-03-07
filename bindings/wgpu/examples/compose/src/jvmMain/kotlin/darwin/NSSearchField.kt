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
import org.rococoa.cocoa.foundation.NSRect

abstract class NSSearchField : NSTextField() {
	interface _Class : ObjCClass {
		open fun alloc(): NSSearchField
	}

	abstract override fun init(): NSSearchField

	abstract override fun initWithFrame(frameRect: NSRect?): NSSearchField

	/**
	 * When the value of this property is YES, the cell calls its action method immediately upon notification of any
	 * changes to the search field. When the value is NO, the cell pauses briefly after receiving a notification and
	 * then calls its action method. Pausing gives the user an opportunity to type more text into the search field
	 * and minimize the number of searches that are performed.
	 *
	 * @param flag A Boolean value indicating whether the cell calls its action method immediately when an
	 * appropriate action occurs.
	 */
	abstract fun setSendsSearchStringImmediately(flag: Boolean)

	abstract fun sendsSearchStringImmediately(): Boolean

	/**
	 * When the value of this property is YES, the cell calls its action method when the user clicks the search button
	 * or presses Return. When the value is NO, the cell calls the action method after each keystroke.
	 * The default value of this property is NO.
	 *
	 * @param flag A Boolean value indicating whether the cell calls its search action method
	 * when the user clicks the search button (or presses Return) or after each keystroke.
	 */
	abstract fun setSendsWholeSearchString(flag: Boolean)

	abstract fun sendsWholeSearchString(): Boolean

	companion object {
		private val CLASS: _Class = org.rococoa.Rococoa.createClass("NSSearchField", _Class::class.java)

		fun searchField(): NSSearchField? {
			return CLASS.alloc().init()
		}

		fun searchFieldWithFrame(frameRect: NSRect?): NSSearchField? {
			return CLASS.alloc().initWithFrame(frameRect)
		}
	}
}

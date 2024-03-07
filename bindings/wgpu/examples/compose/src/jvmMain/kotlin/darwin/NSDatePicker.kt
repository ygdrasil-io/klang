package darwin /*
 * Copyright (c) 2002-2017 iterate GmbH. All rights reserved.
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


import org.rococoa.ObjCClass

abstract class NSDatePicker : NSControl() {
	interface _Class : ObjCClass {
		open fun alloc(): NSDatePicker?
	}

	abstract fun dateValue(): NSDate?

	abstract fun setDateValue(value: NSDate?)

	companion object {
		private val CLASS: NSButton._Class =
			org.rococoa.Rococoa.createClass("NSDatePicker", NSButton._Class::class.java)
	}
}

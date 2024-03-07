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

import com.sun.jna.Library
import com.sun.jna.Native

interface AppKitFunctions : Library {
	/**
	 * Original signature : `void NSBeep()`<br></br>
	 * *native declaration : /System/Library/Frameworks/AppKit.framework/Headers/AppKitDefines.h:194*
	 */
	open fun NSBeep()

	companion object {
		val instance: AppKitFunctions = Native.load("AppKit", AppKitFunctions::class.java)
	}
}

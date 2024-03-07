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

/// <i>native declaration : :10</i>
abstract class NSActionCell : NSCell() {
	interface _Class : ObjCClass {
		open fun alloc(): NSActionCell?
	}

	/**
	 * Original signature : `id target()`<br></br>
	 * *native declaration : :30*
	 */
	abstract fun target(): org.rococoa.ID?

	/**
	 * Original signature : `void setTarget(id)`<br></br>
	 * *native declaration : :31*
	 */
	abstract fun setTarget(anObject: org.rococoa.ID?)
}

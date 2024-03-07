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
import org.rococoa.cocoa.foundation.NSUInteger

abstract class NSImageCell : NSCell(), NSCopying {
	interface _Class : ObjCClass {
		open fun alloc(): NSImageCell
	}

	abstract fun init(): NSImageCell?

	/**
	 * Original signature : `NSImageAlignment imageAlignment()`<br></br>
	 * *native declaration : :51*
	 */
	abstract fun imageAlignment(): NSUInteger?

	/**
	 * Original signature : `void setImageAlignment(NSImageAlignment)`<br></br>
	 * *native declaration : :52*
	 */
	abstract fun setImageAlignment(newAlign: NSUInteger?)

	/**
	 * Original signature : `imageScaling()`<br></br>
	 * *native declaration : :53*
	 */
	abstract fun imageScaling(): NSUInteger?
	/**
	 * *native declaration : :54*<br></br>
	 * Conversion Error : /// Original signature : `void setImageScaling(null)`<br></br>
	 * - (void)setImageScaling:(null)newScaling; (Argument newScaling cannot be converted)
	 */
	/**
	 * Original signature : `NSImageFrameStyle imageFrameStyle()`<br></br>
	 * *native declaration : :55*
	 */
	abstract fun imageFrameStyle(): NSUInteger?

	/**
	 * Original signature : `void setImageFrameStyle(NSImageFrameStyle)`<br></br>
	 * *native declaration : :56*
	 */
	abstract fun setImageFrameStyle(newStyle: NSUInteger?)

	companion object {
		val CLASS: _Class = org.rococoa.Rococoa.createClass("NSImageCell", _Class::class.java)

		fun imageCell(): NSImageCell? {
			return CLASS.alloc().init()
		}
	}
}


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

import org.rococoa.cocoa.foundation.NSUInteger

abstract class NSImageView : NSObject() {
	/**
	 * Original signature : `NSImage* image()`<br></br>
	 * *native declaration : :26*
	 */
	abstract fun image(): NSImage?

	/**
	 * Original signature : `void setImage(NSImage*)`<br></br>
	 * *native declaration : :27*
	 */
	abstract fun setImage(newImage: NSImage?)

	/**
	 * Original signature : `imageAlignment()`<br></br>
	 * *native declaration : :29*
	 */
	abstract fun imageAlignment(): NSUInteger?
	/**
	 * *native declaration : :30*<br></br>
	 * Conversion Error : /// Original signature : `void setImageAlignment(null)`<br></br>
	 * - (void)setImageAlignment:(null)newAlign; (Argument newAlign cannot be converted)
	 */
	/**
	 * Original signature : `imageScaling()`<br></br>
	 * *native declaration : :31*
	 */
	abstract fun imageScaling(): NSUInteger?
	/**
	 * *native declaration : :32*<br></br>
	 * Conversion Error : /// Original signature : `void setImageScaling(null)`<br></br>
	 * - (void)setImageScaling:(null)newScaling; (Argument newScaling cannot be converted)
	 */
	/**
	 * Original signature : `imageFrameStyle()`<br></br>
	 * *native declaration : :33*
	 */
	abstract fun imageFrameStyle(): NSUInteger?
	/**
	 * *native declaration : :34*<br></br>
	 * Conversion Error : /// Original signature : `void setImageFrameStyle(null)`<br></br>
	 * - (void)setImageFrameStyle:(null)newStyle; (Argument newStyle cannot be converted)
	 */
	/**
	 * Original signature : `void setEditable(BOOL)`<br></br>
	 * *native declaration : :35*
	 */
	abstract fun setEditable(editable: Boolean)

	/**
	 * Original signature : `BOOL isEditable()`<br></br>
	 * *native declaration : :36*
	 */
	abstract fun isEditable(): Boolean

	/**
	 * Original signature : `void setAnimates(BOOL)`<br></br>
	 * *native declaration : :39*
	 */
	abstract fun setAnimates(flag: Boolean)

	/**
	 * Original signature : `BOOL animates()`<br></br>
	 * *native declaration : :40*
	 */
	abstract fun animates(): Boolean

	/**
	 * Original signature : `BOOL allowsCutCopyPaste()`<br></br>
	 * *native declaration : :44*
	 */
	abstract fun allowsCutCopyPaste(): Boolean

	/**
	 * Original signature : `void setAllowsCutCopyPaste(BOOL)`<br></br>
	 * *native declaration : :45*
	 */
	abstract fun setAllowsCutCopyPaste(allow: Boolean)
}

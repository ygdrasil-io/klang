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

import org.rococoa.cocoa.foundation.NSPoint
import org.rococoa.cocoa.foundation.NSUInteger

interface NSDraggingSource {
	/**
	 * Original signature : `NSDragOperation draggingSourceOperationMaskForLocal(BOOL)`<br></br>
	 * *native declaration : line 72*
	 */
	open fun draggingSourceOperationMaskForLocal(flag: Boolean): NSUInteger?

	/**
	 * *native declaration : line 76*<br></br>
	 * Conversion Error : /// Original signature : `void draggedImage(NSImage*, null)`<br></br>
	 * - (void)draggedImage:(NSImage*)image beganAt:(null)screenPoint; (Argument screenPoint cannot be converted)
	 */
	open fun draggedImage_beganAt(image: NSImage?, point: NSPoint?)

	/**
	 * *native declaration : line 77*<br></br>
	 * Conversion Error : /// Original signature : `void draggedImage(NSImage*, null, NSDragOperation)`<br></br>
	 * - (void)draggedImage:(NSImage*)image endedAt:(null)screenPoint operation:(NSDragOperation)operation; (Argument screenPoint cannot be converted)
	 */
	open fun draggedImage_endedAt_operation(image: NSImage?, point: NSPoint?, operation: NSUInteger?)

	/**
	 * *native declaration : line 78*<br></br>
	 * Conversion Error : /// Original signature : `void draggedImage(NSImage*, null)`<br></br>
	 * - (void)draggedImage:(NSImage*)image movedTo:(null)screenPoint; (Argument screenPoint cannot be converted)
	 */
	open fun draggedImage_movedTo(image: NSImage?, point: NSPoint?)

	/**
	 * Original signature : `BOOL ignoreModifierKeysWhileDragging()`<br></br>
	 * *native declaration : line 79*
	 */
	open fun ignoreModifierKeysWhileDragging(): Boolean

	open fun namesOfPromisedFilesDroppedAtDestination(dropDestination: NSURL?): NSArray?
}

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

/// <i>native declaration : :32</i>
abstract class NSDraggingInfo : NSObject() {
	/**
	 * Original signature : `NSWindow* draggingDestinationWindow()`<br></br>
	 * *native declaration : :33*
	 */
	abstract fun draggingDestinationWindow(): NSWindow?

	/**
	 * Original signature : `NSDragOperation draggingSourceOperationMask()`<br></br>
	 * *native declaration : :34*
	 */
	abstract fun draggingSourceOperationMask(): NSUInteger?

	/**
	 * Original signature : `draggingLocation()`<br></br>
	 * *native declaration : :35*
	 */
	abstract fun draggingLocation(): NSPoint?

	/**
	 * Original signature : `draggedImageLocation()`<br></br>
	 * *native declaration : :36*
	 */
	abstract fun draggedImageLocation(): NSPoint?

	/**
	 * Original signature : `NSImage* draggedImage()`<br></br>
	 * *native declaration : :37*
	 */
	abstract fun draggedImage(): NSImage?

	/**
	 * Original signature : `NSPasteboard* draggingPasteboard()`<br></br>
	 * *native declaration : :38*
	 */
	abstract fun draggingPasteboard(): NSPasteboard?

	/**
	 * Original signature : `draggingSource()`<br></br>
	 * *native declaration : :39*
	 */
	abstract fun draggingSource(): NSObject?

	/**
	 * Original signature : `NSInteger draggingSequenceNumber()`<br></br>
	 * *native declaration : :40*
	 */
	abstract fun draggingSequenceNumber(): Int
	/**
	 * *native declaration : :41*<br></br>
	 * Conversion Error : /// Original signature : `void slideDraggedImageTo(null)`<br></br>
	 * - (void)slideDraggedImageTo:(null)screenPoint; (Argument screenPoint cannot be converted)
	 */
	/**
	 * Original signature : `NSArray* namesOfPromisedFilesDroppedAtDestination(NSURL*)`<br></br>
	 * *native declaration : :43*
	 */
	abstract fun namesOfPromisedFilesDroppedAtDestination(dropDestination: NSURL?): NSArray?

	companion object {
		/// <i>native declaration : line 15</i>
		val NSDragOperationNone: NSUInteger? = NSUInteger(0)

		/// <i>native declaration : line 16</i>
		val NSDragOperationCopy: NSUInteger? = NSUInteger(1)

		/// <i>native declaration : line 17</i>
		val NSDragOperationLink: NSUInteger? = NSUInteger(2)

		/// <i>native declaration : line 18</i>
		val NSDragOperationGeneric: NSUInteger? = NSUInteger(4)

		/// <i>native declaration : line 19</i>
		val NSDragOperationPrivate: NSUInteger? = NSUInteger(8)

		/// <i>native declaration : line 21</i>
		val NSDragOperationMove: NSUInteger? = NSUInteger(16)

		/// <i>native declaration : line 22</i>
		val NSDragOperationDelete: NSUInteger? = NSUInteger(32)
	}
}

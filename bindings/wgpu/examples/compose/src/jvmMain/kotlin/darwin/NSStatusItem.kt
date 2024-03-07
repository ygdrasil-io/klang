package darwin /*
 * Copyright (c) 2002-2015 David Kocher. All rights reserved.
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
 * Bug fixes, suggestions and comments should be sent to feedback@cyberduck.ch
 */

import org.rococoa.cocoa.CGFloat

abstract class NSStatusItem : NSObject() {
	/**
	 * @return The status bar in which the status item is displayed.
	 */
	abstract fun statusBar(): NSStatusBar?

	/**
	 * If the status bar is horizontal, the value of this property is the width of the status item.
	 * In addition to a fixed length, this value can be NSSquareStatusItemLength or NSVariableStatusItemLength
	 * (see NSStatusBar Constants) to allow the status bar to allocate (and adjust) the space according to either
	 * the status bar’s thickness or the status item’s true size.
	 *
	 * @return The amount of space in the status bar that should be allocated to the status item.
	 */
	abstract fun length(): CGFloat?

	/**
	 * @param length The amount of space in the status bar that should be allocated to the status item.
	 */
	abstract fun setLength(length: CGFloat?)

	/**
	 * The pull-down menu that is displayed when the status item is clicked.
	 */
	abstract fun menu(): NSMenu?

	abstract fun setMenu(menu: NSMenu?)

	/**
	 * This button is created automatically on the creation of the status item. Behavior customization for the
	 * button, such as image, target, action, tooltip, and so on can be set with this property.
	 */
	abstract fun button(): NSStatusBarButton?

}

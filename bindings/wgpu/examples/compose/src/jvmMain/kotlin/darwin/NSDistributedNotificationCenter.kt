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

/// <i>native declaration : :27</i>
abstract class NSDistributedNotificationCenter : NSNotificationCenter() {
	interface _Class : ObjCClass {
		/**
		 * Original signature : `NSDistributedNotificationCenter* notificationCenterForType(NSString*)`<br></br>
		 * *native declaration : :29*
		 */
		open fun notificationCenterForType(notificationCenterType: String?): NSDistributedNotificationCenter?

		/**
		 * Original signature : `defaultCenter()`<br></br>
		 * *native declaration : :32*
		 */
		open fun defaultCenter(): NSDistributedNotificationCenter?
	}

	/**
	 * Original signature : `void postNotificationName(NSString*, NSString*, NSDictionary*, BOOL)`<br></br>
	 * *native declaration : :38*
	 */
	abstract fun postNotificationName_object_userInfo_deliverImmediately(
		name: String?,
		`object`: String?,
		userInfo: NSDictionary?,
		deliverImmediately: Boolean
	)

	companion object {
		private val CLASS: _Class =
			org.rococoa.Rococoa.createClass("NSDistributedNotificationCenter", _Class::class.java)

		fun defaultCenter(): NSNotificationCenter? {
			return CLASS.defaultCenter()
		}
	}
}

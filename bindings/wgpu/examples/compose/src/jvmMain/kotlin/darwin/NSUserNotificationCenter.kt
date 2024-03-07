package darwin /*
 * Copyright (c) 2002-2012 David Kocher. All rights reserved.
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

import org.rococoa.ID
import org.rococoa.ObjCClass

abstract class NSUserNotificationCenter : NSObject() {
	interface _Class : ObjCClass {
		/**
		 * Get a singleton user notification center that posts notifications for this process.<br></br> Original signature :
		 * `+(NSUserNotificationCenter*)defaultUserNotificationCenter`<br></br>
		 * *native declaration : line 8*
		 */
		open fun defaultUserNotificationCenter(): NSUserNotificationCenter?
	}

	// Add a notification to the center for scheduling.
	abstract fun scheduleNotification(notification: NSUserNotification?)

	// Cancels a notification. If the deliveryDate occurs before the cancellation finishes, the notification
	// may still be delivered. If the notification is not in the scheduled list, nothing happens.
	abstract fun removeScheduledNotification(notification: NSUserNotification?)

	abstract fun removeAllDeliveredNotifications()

	abstract fun setDelegate(delegate: ID?)

	interface Delegate {
		open fun userNotificationCenter_didActivateNotification(
			center: NSUserNotificationCenter?,
			notification: NSUserNotification?
		)

		open fun userNotificationCenter_shouldPresentNotification(
			center: NSUserNotificationCenter?,
			notification: NSUserNotification?
		): Boolean
	}

	companion object {
		private val CLASS: _Class = org.rococoa.Rococoa.createClass("NSUserNotificationCenter", _Class::class.java)

		/**
		 * Get a singleton user notification center that posts notifications for this process.<br></br> Original signature :
		 * `+(NSUserNotificationCenter*)defaultUserNotificationCenter`<br></br>
		 * *native declaration : line 8*
		 */
		fun defaultUserNotificationCenter(): NSUserNotificationCenter? {
			return CLASS.defaultUserNotificationCenter()
		}
	}
}

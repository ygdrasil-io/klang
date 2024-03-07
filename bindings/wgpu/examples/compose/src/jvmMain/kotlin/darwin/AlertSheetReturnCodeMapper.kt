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

import org.apache.logging.log4j.LogManager

class AlertSheetReturnCodeMapper {
	/**
	 * Translate return codes from sheet selection
	 *
	 * @param sender Button pressed
	 * @return Sheet callback constant
	 * @see SheetCallback.DEFAULT_OPTION
	 *
	 * @see SheetCallback.CANCEL_OPTION
	 */
	fun getOption(sender: NSButton?): Int {
		return this.getOption(sender?.tag() ?: 0)
	}

	fun getOption(option: Int): Int {
		when (option) {
			NSAlert.NSAlertFirstButtonReturn, NSPanel.NSOKButton -> return SheetCallback.DEFAULT_OPTION
			NSAlert.NSAlertSecondButtonReturn, NSPanel.NSCancelButton -> return SheetCallback.CANCEL_OPTION
			NSAlert.NSAlertThirdButtonReturn -> return SheetCallback.ALTERNATE_OPTION
		}
		log.warn(String.format("Unknown return code %d", option))
		return SheetCallback.DEFAULT_OPTION
	}

	companion object {
		private val log = LogManager.getLogger(AlertSheetReturnCodeMapper::class.java)
	}
}

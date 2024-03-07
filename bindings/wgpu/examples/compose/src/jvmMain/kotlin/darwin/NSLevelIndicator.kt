package darwin /*
 * Copyright (c) 2002-2016 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
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
 */

import org.rococoa.ObjCClass
import org.rococoa.cocoa.foundation.NSRect

abstract class NSLevelIndicator : NSControl() {
	interface _Class : ObjCClass {
		open fun alloc(): NSLevelIndicator
	}

	abstract override fun initWithFrame(frameRect: NSRect?): NSLevelIndicator

	abstract fun minValue(): Int

	abstract fun setMinValue(minValue: Int)

	abstract fun maxValue(): Int

	abstract fun setMaxValue(maxValue: Int)

	abstract fun warningValue(): Int

	abstract fun setWarningValue(warningValue: Int)

	abstract fun criticalValue(): Int

	abstract fun setCriticalValue(criticalValue: Int)

	abstract fun tickMarkPosition(): Int

	abstract fun setTickMarkPosition(tickMarkPosition: Int)

	abstract fun numberOfTickMarks(): Int

	abstract fun setNumberOfTickMarks(numberOfTickMarks: Int)

	abstract fun levelIndicatorStyle(): Int

	abstract fun setLevelIndicatorStyle(levelIndicatorStyle: Int)

	companion object {
		private val CLASS: _Class = org.rococoa.Rococoa.createClass("NSLevelIndicator", _Class::class.java)

		const val NSRelevancyLevelIndicatorStyle: Int = 0
		const val NSContinuousCapacityLevelIndicatorStyle: Int = 1
		const val NSDiscreteCapacityLevelIndicatorStyle: Int = 2
		const val NSRatingLevelIndicatorStyle: Int = 3

		fun levelIndicatorWithFrame(frameRect: NSRect?): NSLevelIndicator? {
			return CLASS.alloc().initWithFrame(frameRect)
		}
	}
}

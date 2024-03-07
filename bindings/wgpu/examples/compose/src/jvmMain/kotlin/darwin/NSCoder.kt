package darwin /*
 * Copyright (c) 2002-2010 David Kocher. All rights reserved.
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

import com.sun.jna.Pointer
import org.rococoa.cocoa.foundation.NSInteger

abstract class NSCoder : NSObject() {
	/**
	 * Original signature : `-(void)encodeValueOfObjCType:(const char*) at:(const void*)`<br></br>
	 * *native declaration : NSCoder.h:12*
	 */
	abstract fun encodeValueOfObjCType_at(type: String?, addr: Pointer?)

	/**
	 * Original signature : `-(void)encodeDataObject:(NSData*)`<br></br>
	 * *native declaration : NSCoder.h:13*
	 */
	abstract fun encodeDataObject(data: NSData?)

	/**
	 * Original signature : `-(void)decodeValueOfObjCType:(const char*) at:(void*)`<br></br>
	 * *native declaration : NSCoder.h:14*
	 */
	abstract fun decodeValueOfObjCType_at(type: String?, data: Pointer?)

	/**
	 * Original signature : `-(NSData*)decodeDataObject`<br></br>
	 * *native declaration : NSCoder.h:15*
	 */
	abstract fun decodeDataObject(): NSData?

	/**
	 * Original signature : `-(NSInteger)versionForClassName:(NSString*)`<br></br>
	 * *native declaration : NSCoder.h:16*
	 */
	abstract fun versionForClassName(className: NSString?): NSInteger? /// <i>native declaration : NSCoder.h</i>
}

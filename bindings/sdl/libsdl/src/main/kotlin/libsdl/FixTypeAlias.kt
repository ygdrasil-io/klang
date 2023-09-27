package libsdl

import com.sun.jna.Pointer
import com.sun.jna.PointerType
import com.sun.jna.ptr.PointerByReference

typealias SDL_iconv_t = Pointer
typealias SDL_JoystickGUID = Pointer

public class SDL_Haptic : PointerType {
	public constructor() : super()

	public constructor(pointer: Pointer?) : super(pointer)

	public class ByReference : PointerByReference {
		public constructor() : super()

		public constructor(pointer: Pointer?) : super(pointer)
	}
}

public class SDL_AudioStream : PointerType {
	public constructor() : super()

	public constructor(pointer: Pointer?) : super(pointer)

	public class ByReference : PointerByReference {
		public constructor() : super()

		public constructor(pointer: Pointer?) : super(pointer)
	}
}

public class SDL_hid_device : PointerType {
	public constructor() : super()

	public constructor(pointer: Pointer?) : super(pointer)

	public class ByReference : PointerByReference {
		public constructor() : super()

		public constructor(pointer: Pointer?) : super(pointer)
	}
}

public class SDL_GameController : PointerType {
	public constructor() : super()

	public constructor(pointer: Pointer?) : super(pointer)

	public class ByReference : PointerByReference {
		public constructor() : super()

		public constructor(pointer: Pointer?) : super(pointer)
	}
}

public class SDL_Sensor : PointerType {
	public constructor() : super()

	public constructor(pointer: Pointer?) : super(pointer)

	public class ByReference : PointerByReference {
		public constructor() : super()

		public constructor(pointer: Pointer?) : super(pointer)
	}
}

public class SDL_sem : PointerType {
	public constructor() : super()

	public constructor(pointer: Pointer?) : super(pointer)

	public class ByReference : PointerByReference {
		public constructor() : super()

		public constructor(pointer: Pointer?) : super(pointer)
	}
}

public class SDL_Joystick : PointerType {
	public constructor() : super()

	public constructor(pointer: Pointer?) : super(pointer)

	public class ByReference : PointerByReference {
		public constructor() : super()

		public constructor(pointer: Pointer?) : super(pointer)
	}
}
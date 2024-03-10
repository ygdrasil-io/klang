package my.dummy

import korlibs.math.geom.Angle
import korlibs.math.geom.Matrix4

@JsExport
class Dummy {

	fun dumb() = dummy

	companion object {
		val dummy: String = "dummy"
		fun dumb() = dummy

		fun persepective(fovy: Angle, aspect: Double, zNear: Double, zFar: Double) =
			Matrix4.perspective(fovy, aspect, zNear, zFar)

	}
}

@JsExport
fun fromRadians(radians: Float) = Angle.fromRadians(radians)
package example.toolkit

import com.sun.jna.Memory
import com.sun.jna.ptr.IntByReference
import libangle.GLuint
import libgles.*
import java.nio.ByteBuffer

fun CompileProgram(vsSource: String, fsSource: String): GLuint {
	return CompileProgramInternal(vsSource, "", "", "", fsSource)
}

fun CompileProgramInternal(
	vsSource: String,
	tcsSource: String,
	tesSource: String,
	gsSource: String,
	fsSource: String
) : GLuint {
	val program = libGLESv2Library.glCreateProgram()

	val vs = CompileShader(GL_VERTEX_SHADER, vsSource)
	val fs = CompileShader(GL_FRAGMENT_SHADER, fsSource)

	if (vs == 0 || fs == 0) {
		libGLESv2Library.glDeleteShader(fs)
		libGLESv2Library.glDeleteShader(vs)
		libGLESv2Library.glDeleteProgram(program)
		return 0
	}

	libGLESv2Library.glAttachShader(program, vs)
	libGLESv2Library.glDeleteShader(vs)

	libGLESv2Library.glAttachShader(program, fs)
	libGLESv2Library.glDeleteShader(fs)

	var tcs = 0
	var tes = 0
	var gs = 0

	if (tcsSource.isNotEmpty()) {
		tcs = CompileShader(GL_TESS_CONTROL_SHADER_EXT, tcsSource)
		if (tcs == 0) {
			libGLESv2Library.glDeleteShader(vs)
			libGLESv2Library.glDeleteShader(fs)
			libGLESv2Library.glDeleteProgram(program)
			return 0
		}

		libGLESv2Library.glAttachShader(program, tcs)
		libGLESv2Library.glDeleteShader(tcs)
	}

	if (tesSource.isNotEmpty()) {
		tes = CompileShader(GL_TESS_EVALUATION_SHADER_EXT, tesSource)
		if (tes == 0) {
			libGLESv2Library.glDeleteShader(vs)
			libGLESv2Library.glDeleteShader(fs)
			libGLESv2Library.glDeleteShader(tcs)
			libGLESv2Library.glDeleteProgram(program)
			return 0
		}

		libGLESv2Library.glAttachShader(program, tes)
		libGLESv2Library.glDeleteShader(tes)
	}

	if (gsSource.isNotEmpty()) {
		gs = CompileShader(GL_GEOMETRY_SHADER_EXT, gsSource)
		if (gs == 0) {
			libGLESv2Library.glDeleteShader(vs)
			libGLESv2Library.glDeleteShader(fs)
			libGLESv2Library.glDeleteShader(tcs)
			libGLESv2Library.glDeleteShader(tes)
			libGLESv2Library.glDeleteProgram(program)
			return 0
		}

		libGLESv2Library.glAttachShader(program, gs)
		libGLESv2Library.glDeleteShader(gs)
	}

	libGLESv2Library.glLinkProgram(program)

	return CheckLinkStatusAndReturnProgram(program, true)
}

fun CompileShader(type: GLenum, source: String): GLuint {
	val shader = libGLESv2Library.glCreateShader(type)

	libGLESv2Library.glShaderSource(shader, 1, source, null)
	libGLESv2Library.glCompileShader(shader)

	val compileResult = IntByReference()
	libGLESv2Library.glGetShaderiv(shader, GL_COMPILE_STATUS, compileResult.pointer)

	if (compileResult.value == 0) {
		val infoLogLength = IntByReference()
		libGLESv2Library.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, infoLogLength.pointer)

		// Info log length includes the null terminator, so 1 means that the info log is an empty
		// string.
		if (infoLogLength.value > 1) {
			val infoLog = Memory(infoLogLength.value.toLong())
			libGLESv2Library.glGetShaderInfoLog(shader, infoLogLength.value, null, infoLog)
			System.err.println("program link failed: " + infoLog.getString(0L))
		} else {
			System.err.println("program link failed. <Empty log message>")
		}

		libGLESv2Library.glDeleteShader(shader)
		return 0
	}

	return shader
}

fun CheckLinkStatusAndReturnProgram(program: GLuint, outputErrorMessages: Boolean): GLuint {
	if (libGLESv2Library.glGetError() != GL_NO_ERROR)
		return 0

	val linkStatus = IntByReference()
	libGLESv2Library.glGetProgramiv(program, GL_LINK_STATUS, linkStatus.pointer)
	if (linkStatus.value == 0) {
		if (outputErrorMessages) {
			val infoLogLength = IntByReference()
			libGLESv2Library.glGetProgramiv(program, GL_INFO_LOG_LENGTH, infoLogLength.pointer)

			// Info log length includes the null terminator, so 1 means that the info log is an
			// empty string.
			if (infoLogLength.value > 1) {
				val infoLog = Memory(infoLogLength.value.toLong())
				libGLESv2Library.glGetProgramInfoLog(program, infoLogLength.value, null, infoLog)
				System.err.println("program link failed: " + infoLog.getString(0L))
			} else {
				System.err.println("program link failed. <Empty log message>")
			}
		}

		libGLESv2Library.glDeleteProgram(program)
		return 0
	}

	return program
}
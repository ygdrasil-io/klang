package example

import com.sun.jna.Memory
import example.toolkit.CompileProgram
import example.toolkit.SampleApplication
import libangle.libEGLLibrary
import libgles.*

class HelloTriangle : SampleApplication("HelloTriangle") {
	private var mProgram: GLuint = 0

	override fun initialize(): Boolean {
		val kVS = """
		attribute vec4 vPosition;
		
		void main()
		{
			gl_Position = vPosition;
		}"""

		val kFS = """precision mediump float;
		void main()
		{
			libEGLLibrary.gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
		})"""

		mProgram = CompileProgram(kVS, kFS);
		if (mProgram == 0) {
			return false
		}


		libGLESv2Library.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		return true;
	}

	override fun destroy()  { libGLESv2Library.glDeleteProgram(mProgram); }

	override fun draw() {
		val vertices = floatArrayOf(
			0.0f, 0.5f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f,
		)
		val verticesBuffer = Memory(vertices.size * 4L).apply {
			write(0, vertices, 0, vertices.size)
		}

		// Set the viewport
		libGLESv2Library.glViewport(0, 0, window.width, window.height)

		// Clear the color buffer
		libGLESv2Library.glClear(GL_COLOR_BUFFER_BIT)

		// Use the program object
		libGLESv2Library.glUseProgram(mProgram)

		// Load the vertex data
		libGLESv2Library.glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE.toByte(), 0, verticesBuffer)
		libGLESv2Library.glEnableVertexAttribArray(0)

		libGLESv2Library.glDrawArrays(GL_TRIANGLES, 0, 3)
	}

}


fun main() {
	HelloTriangle()
		.run()
}
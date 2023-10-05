package example

import example.toolkit.CompileProgram
import example.toolkit.SampleApplication
import libangle.libEGLLibrary

class HelloTriangle : SampleApplication("HelloTriangle") {
	private var mProgram: UInt? = null

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
		if (!mProgram)
		{
			return false;
		}

		libEGLLibrary.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		return true;
	}

	override fun destroy()  { glDeleteProgram(mProgram); }

	override draw() {
		val vertices = listOf(
			0.0f, 0.5f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f,
		)

		// Set the viewport
		glViewport(0, 0, getWindow()->getWidth(), getWindow()->getHeight());

		// Clear the color buffer
		glClear(GL_COLOR_BUFFER_BIT);

		// Use the program object
		glUseProgram(mProgram);

		// Load the vertex data
		glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, vertices);
		glEnableVertexAttribArray(0);

		glDrawArrays(GL_TRIANGLES, 0, 3);
	}

}


fun main() {
	HelloTriangle()
		.run()
}
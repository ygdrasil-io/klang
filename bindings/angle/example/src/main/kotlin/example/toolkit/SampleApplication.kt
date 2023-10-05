package example.toolkit

import javax.swing.JFrame

abstract class SampleApplication(title: String) {

	val window = OSWindow(title)

	abstract fun initialize(): Boolean
	abstract fun destroy()
	abstract fun draw()


}

class OSWindow(title: String): JFrame(title) {

	init {
	    preferredSize = java.awt.Dimension(800, 600)
		pack()
	}

}
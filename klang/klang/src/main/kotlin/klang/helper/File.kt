package klang.helper

import java.io.File


/**
 * Checks if the file does not exist.
 *
 * @return true if the file does not exist, false otherwise.
 */
fun File.doesNotExists(): Boolean  = exists().not()

/**
 * Checks if the given directory is empty.
 *
 * @return true if the directory is empty, false otherwise.
 * @throws IllegalStateException if the file is not a directory.
 */
fun File.isDirectoryEmpty(): Boolean {
	if (isDirectory.not()) error("$this is not a directory")
	return listFiles()?.any() != true
}

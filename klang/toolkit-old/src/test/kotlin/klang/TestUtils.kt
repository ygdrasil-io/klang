package klang

import io.kotest.matchers.shouldBe
import java.io.*

val LINE_SEPARATOR = System.getProperty("line.separator")

fun getTestDataDir(): File {
    return File("testData/")
}

fun createOrCompare(actual: String, expectedFileName: String) {
    try {
        val expected = loadFileContents(expectedFileName)
        actual shouldBe expected
    } catch (e: IOException) {
        try {
            val out = PrintWriter(File(expectedFileName))
            out.print(actual)
            out.close()
			check(actual == "") {
                "Expected file wasn't found, it will be created"
            }
        } catch (ee: IOException) {
            ee.printStackTrace()
            error(ee.message ?: "")
        }
    }
}

@Throws(IOException::class)
fun loadFileContents(fileName: String): String {
    val reader = BufferedReader(FileReader(fileName))
    val sb = StringBuilder()
    var s: String?
    while (reader.readLine().also { s = it } != null) {
        sb.append(s).append(LINE_SEPARATOR)
    }
    reader.close()
    return sb.toString()
}

fun createTempFileWithContents(contents: String): File {
    return try {
        val dummy = File.createTempFile("jclang", ".h")
        val out = PrintWriter(dummy)
        out.print(contents)
        out.close()
        dummy
    } catch (e: Exception) {
        throw rethrow(e)
    }
}
fun rethrow(e: java.lang.Exception): RuntimeException {
    throw if (e is RuntimeException) e else RuntimeException(e)
}
package klang

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

val githubUrl = ""

abstract class SharedLibraryProviderTask : DefaultTask() {

	@TaskAction
	fun downloadSharedLibraryProviderIfNeeded() {
		TODO("Not yet implemented")
	}
}
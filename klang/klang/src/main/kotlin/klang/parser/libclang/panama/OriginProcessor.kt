package klang.parser.libclang.panama

import klang.domain.DeclarationOrigin
import org.openjdk.jextract.Position
import java.nio.file.Path
import kotlin.io.path.absolutePathString

object OriginProcessor {

	internal fun Position?.toOrigin(filePath: Path?): DeclarationOrigin = when {
		filePath == null || this == null -> DeclarationOrigin.Unknown
		else -> when {
			isInFilePath(filePath) -> DeclarationOrigin.LibraryHeader(path().absolutePathString())
			else -> DeclarationOrigin.PlatformHeader
		}
	}

	private fun Position.isInFilePath(filePath: Path) =
		path().absolutePathString().contains(filePath.absolutePathString())

}
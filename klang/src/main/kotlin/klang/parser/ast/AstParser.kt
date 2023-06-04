@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class)

package klang.parser.ast

import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

fun parseAst(filePath: String): Nothing = rawAst(File(filePath))
	.let { TODO() }
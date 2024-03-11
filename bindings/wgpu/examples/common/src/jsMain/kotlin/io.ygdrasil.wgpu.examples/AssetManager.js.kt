package io.ygdrasil.wgpu.examples

import kotlinx.coroutines.await
import org.w3c.files.Blob
import kotlin.js.JSON.stringify
import kotlin.js.Promise

actual object AssetManager {

	actual suspend fun getResource(path: String): ByteArray {
		return fetch(path).await()
			.also { println(stringify(it)) }
			.unsafeCast<ByteArray>()
	}


}

external fun fetch(path: String): Promise<Blob>
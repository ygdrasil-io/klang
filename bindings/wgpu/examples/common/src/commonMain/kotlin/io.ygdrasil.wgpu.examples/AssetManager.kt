package io.ygdrasil.wgpu.examples


expect object AssetManager {

	suspend fun getResource(path: String): ByteArray
}
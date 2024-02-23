package io.ygdrasil.triangle

import io.ygdrasil.libsdl.*


fun main() {
	if (SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
		error("SDL_Init Error: ${SDL_GetError()}")
	}

	val window = SDL_CreateWindow(
		"", SDL_WINDOWPOS_CENTERED.toInt(),
		SDL_WINDOWPOS_CENTERED.toInt(), 1, 1,
		SDL_WindowFlags.SDL_WINDOW_SHOWN.value
	) ?: error("fail to create window ${SDL_GetError()}")

	val renderer = SDL_CreateRenderer(
		window, -1, SDL_RendererFlags.SDL_RENDERER_ACCELERATED or SDL_RendererFlags.SDL_RENDERER_PRESENTVSYNC
	) ?: error("fail to create renderer")


	while (true) {

		SDL_RenderClear(renderer)
		SDL_SetRenderDrawColor(
			renderer,
			(200 / 2).toByte(),
			(230 / 2).toByte(),
			(151 / 2).toByte(),
			SDL_ALPHA_OPAQUE.toByte()
		)

		SDL_RenderPresent(renderer)

		val event = SDL_Event()
		while (SDL_PollEvent(event) != 0) {

			SDL_PollEvent(event)
		}
	}
}
package snake

import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import libsdl.*
import sdl.App
import sdl.AppContext
import sdl.allocRect
import java.io.File

fun main() = App().use { app ->


	SnakeView(app, initialGameState.width, initialGameState.height).use { view ->

		var game = initialGameState
		var ticks = 0
		val speed = 10

		while (true) {

			view.draw(game)

			view.delay(1000 / 60)
			ticks++
			if (ticks >= speed) {
				game = game.update()
				ticks = 0
			}

			view.readCommands().forEach { command ->
				var direction: Direction? = null
				when (command) {
					SnakeView.UserCommand.up -> direction = Direction.up
					SnakeView.UserCommand.down -> direction = Direction.down
					SnakeView.UserCommand.left -> direction = Direction.left
					SnakeView.UserCommand.right -> direction = Direction.right
					SnakeView.UserCommand.restart -> game = initialGameState
					SnakeView.UserCommand.quit -> return
				}
				game = game.update(direction)
				view.draw(game)
			}
		}
	}
}

class SnakeView(
	context: AppContext,
	width: Int,
	height: Int
) : AutoCloseable, AppContext by context {

	private val controller: SDL_GameController?
	private val font: Font
	private val sprites: Sprites
	private val pixelWidth = width * Sprites.w
	private val pixelHeight = height * Sprites.h


	init {

		libSDL2Library.SDL_SetWindowSize(window, pixelWidth, pixelHeight)
		libSDL2Library.SDL_SetWindowTitle(window, "snake")

		controller = when (libSDL2Library.SDL_NumJoysticks() != 0) {
			true -> libSDL2Library.SDL_GameControllerOpen(0)
			false -> null
		}

		font = Font(context)
		sprites = Sprites(context)

		playMusic()
	}

	fun draw(game: Game) {
		libSDL2Library.SDL_RenderClear(renderer)
		libSDL2Library.SDL_SetRenderDrawColor(
			renderer,
			(200 / 2).toByte(),
			(230 / 2).toByte(),
			(151 / 2).toByte(),
			SDL_ALPHA_OPAQUE.toByte()
		)

		val grassW = 256
		val grassScaledW = 400 // scale grass up to reduce its resolution so that it's similar to snake sprites
		0.until(pixelWidth / grassW + 1).forEach { x ->
			0.until(pixelHeight / grassW + 1).forEach { y ->
				sprites.render(sprites.grassRect, allocRect(x * grassW, y * grassW, grassScaledW, grassScaledW))
			}
		}

		game.apples.cells.forEach {
			sprites.render(sprites.appleRect, cellRect(it))
		}

		game.snake.tail.dropLast(1).forEachIndexed { i, it ->
			val index = i + 1
			val direction = direction(from = game.snake.cells[index - 1], to = it)
			val nextDirection = direction(from = it, to = game.snake.cells[index + 1])

			val srcRect = if (direction == nextDirection) {
				when (direction) {
					Direction.right, Direction.left -> sprites.bodyHorRect
					Direction.up, Direction.down -> sprites.bodyVertRect
				}
			} else if ((direction == Direction.left && nextDirection == Direction.down) || (direction == Direction.up && nextDirection == Direction.right)) {
				sprites.bodyLeftDownRect
			} else if ((direction == Direction.left && nextDirection == Direction.up) || (direction == Direction.down && nextDirection == Direction.right)) {
				sprites.bodyLeftUpRect
			} else if ((direction == Direction.right && nextDirection == Direction.down) || (direction == Direction.up && nextDirection == Direction.left)) {
				sprites.bodyRightDownRect
			} else if ((direction == Direction.right && nextDirection == Direction.up) || (direction == Direction.down && nextDirection == Direction.left)) {
				sprites.bodyRightUpRect
			} else {
				sprites.emptyRect
			}
			sprites.render(srcRect, cellRect(it))
		}

		val tipRect = when (game.snake.cells.let { direction(from = it.last(), to = it[it.size - 2]) }) {
			Direction.up -> sprites.tipUpRect
			Direction.down -> sprites.tipDownRect
			Direction.left -> sprites.tipLeftRect
			Direction.right -> sprites.tipRightRect
		}
		sprites.render(tipRect, cellRect(game.snake.tail.last()))

		val headRect = when (game.snake.direction) {
			Direction.up -> sprites.headUpRect
			Direction.down -> sprites.headDownRect
			Direction.left -> sprites.headLeftRect
			Direction.right -> sprites.headRightRect
		}
		sprites.render(headRect, cellRect(game.snake.head))

		if (game.isOver) {
			renderStringCentered(3, game.width, "game over")
			renderStringCentered(5, game.width, "your score is ${game.score}")
		}

		libSDL2Library.SDL_RenderPresent(renderer)

	}

	fun delay(timeMs: Int) {
		libSDL2Library.SDL_Delay(timeMs)
	}

	fun readCommands(): List<UserCommand> {
		val result = ArrayList<UserCommand>()
		val event = SDL_Event()
		while (libSDL2Library.SDL_PollEvent(event) != 0) {
			event.read()
			println("event(${event.type}): ${SDL_EventType.of(event.type)}")
			when (SDL_EventType.of(event.type)) {
				SDL_EventType.SDL_WINDOWEVENT -> {
					val windowEvent = SDL_WindowEventID.of(event.window.event.toInt())
					println("controllerButtonEvent(${windowEvent})")

					if (windowEvent == SDL_WindowEventID.SDL_WINDOWEVENT_SHOWN) {
						//playMusic()
					}
				}

				SDL_EventType.SDL_QUIT -> result.add(UserCommand.quit)
				SDL_EventType.SDL_CONTROLLERBUTTONDOWN -> {
					val controllerButtonEvent = event.cbutton
					val button = controllerButtonEvent.button.toInt()
					println("controllerButtonEvent($button): ${SDL_GameControllerButton.of(button)}")
					val command = when (SDL_GameControllerButton.of(button)) {
						SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_UP -> UserCommand.up
						SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_DOWN -> UserCommand.down
						SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_LEFT -> UserCommand.left
						SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_DPAD_RIGHT -> UserCommand.right
						SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_START -> UserCommand.restart
						SDL_GameControllerButton.SDL_CONTROLLER_BUTTON_BACK -> UserCommand.quit
						else -> null
					}
					if (command != null) result.add(command)
				}

				SDL_EventType.SDL_KEYDOWN -> {
					val keyboardEvent = event.key
					val keysym = keyboardEvent.keysym
					println("keyboardEvent(${keysym.scancode}): ${SDL_Scancode.of(keysym.scancode)}")
					val command = when (SDL_Scancode.of(keysym.scancode)) {
						SDL_Scancode.SDL_SCANCODE_I -> UserCommand.up
						SDL_Scancode.SDL_SCANCODE_J -> UserCommand.left
						SDL_Scancode.SDL_SCANCODE_K -> UserCommand.down
						SDL_Scancode.SDL_SCANCODE_L -> UserCommand.right
						SDL_Scancode.SDL_SCANCODE_R -> UserCommand.restart
						SDL_Scancode.SDL_SCANCODE_Q -> UserCommand.quit
						else -> null
					}
					if (command != null) result.add(command)
				}

				else -> Unit
			}
		}
		return result
	}

	private fun playMusic() {
		val fileName = "Crowander-Stop-on-a-Bench.wav"
		val paths = listOf(fileName, "resources/$fileName", "../resources/$fileName")
		val filePath = paths.find { File(it).canRead() } ?: error("Can't find sound file.")
		val audioFile = libSDL2Library.SDL_RWFromFile(filePath, "rb")
		val audio_spec = SDL_AudioSpec()
		val audio_buf = PointerByReference()
		val audio_len = IntByReference()
		libSDL2Library.SDL_LoadWAV_RW(
			src = audioFile,
			freesrc = 1,
			spec = audio_spec,
			audio_buf.pointer,
			audio_len.pointer
		)

		val deviceName = libSDL2Library.SDL_GetAudioDeviceName(0, 0)
		val device_id = libSDL2Library.SDL_OpenAudioDevice(deviceName, 0, audio_spec, SDL_AudioSpec(), 0)
		libSDL2Library.SDL_QueueAudio(device_id, audio_buf.value, audio_len.value)
		libSDL2Library.SDL_PauseAudioDevice(device_id, 0)
	}

	private fun direction(from: Cell, to: Cell): Direction = when {
		from.x == to.x && from.y > to.y -> Direction.up
		from.x == to.x && from.y < to.y -> Direction.down
		from.x > to.x && from.y == to.y -> Direction.left
		from.x < to.x && from.y == to.y -> Direction.right
		else -> error("")
	}

	private fun cellRect(cell: Cell): SDL_Rect {
		val x = cell.x * Sprites.w
		val y = cell.y * Sprites.h
		return allocRect(x, y, Sprites.w, Sprites.h)
	}

	private fun renderStringCentered(y: Int, width: Int, s: String) {
		var x = (width / 2) - (s.length / 2)
		if (x.rem(2) != 0) x--
		renderString(Cell(x, y), s)
	}

	private fun renderString(atCell: Cell, s: String) {
		s.toCharArray().forEachIndexed { i, c ->
			font.render(c, cellRect(atCell.copy(x = atCell.x + i)))
		}
	}

	enum class UserCommand {
		up, down, left, right, restart, quit
	}


	class Font(context: AppContext) : AppContext by context {
		companion object {
			const val w = 48
			const val h = 46
		}

		internal val texture = addTexture("Font16_42_Normal4_sheet.bmp")
		private val letters: Map<Char, SDL_Rect>

		init {
			letters = mapOf(
				'A' to textureRect(0, 0, -7),
				'B' to textureRect(1, 0),
				'C' to textureRect(2, 0, -9),
				'D' to textureRect(3, 0),
				'E' to textureRect(4, 0, -5),
				'F' to textureRect(5, 0, -5),
				'G' to textureRect(6, 0),
				'H' to textureRect(7, 0, -7),
				'I' to textureRect(8, 0, -15),
				'J' to textureRect(9, 0, -5),
				'K' to textureRect(0, 1, -10),
				'L' to textureRect(1, 1, -5),
				'M' to textureRect(2, 1),
				'N' to textureRect(3, 1),
				'O' to textureRect(4, 1, -7),
				'P' to textureRect(5, 1, -7),
				'Q' to textureRect(6, 1),
				'R' to textureRect(7, 1),
				'S' to textureRect(8, 1),
				'T' to textureRect(9, 1),
				'U' to textureRect(0, 2, -13),
				'V' to textureRect(1, 2, -10),
				'W' to textureRect(2, 2),
				'X' to textureRect(3, 2),
				'Y' to textureRect(4, 2, -5),
				'Z' to textureRect(5, 2),
				'0' to textureRect(2, 5),
				'1' to textureRect(3, 5, -15),
				'2' to textureRect(4, 5),
				'3' to textureRect(5, 5),
				'4' to textureRect(6, 5),
				'5' to textureRect(7, 5),
				'6' to textureRect(8, 5),
				'7' to textureRect(9, 5),
				'8' to textureRect(0, 6),
				'9' to textureRect(1, 6),
				' ' to allocRect(0, 0, 0, 0)
			)
		}

		fun render(char: Char, cellRect: SDL_Rect) {
			val charRect = letters[char.uppercaseChar()] ?: (letters[' '] ?: error(""))
			libSDL2Library.SDL_RenderCopy(renderer, texture, charRect, cellRect)
		}

		private fun textureRect(x: Int, y: Int, wAdjust: Int = 0): SDL_Rect {
			val xShift = x * w
			val yShift = y * h
			return allocRect(xShift, yShift, w + wAdjust, h)
		}
	}

	class Sprites(context: AppContext) : AppContext by context {
		companion object {
			const val w = 64
			const val h = 64
		}

		internal val texture = addTexture("snake-graphics.bmp")
		internal val grassTexture = addTexture("grass.bmp")

		val headUpRect = textureRect(3, 0)
		val headRightRect = textureRect(4, 0)
		val headLeftRect = textureRect(3, 1)
		val headDownRect = textureRect(4, 1)

		val tipUpRect = textureRect(3, 2)
		val tipRightRect = textureRect(4, 2)
		val tipLeftRect = textureRect(3, 3)
		val tipDownRect = textureRect(4, 3)

		val bodyHorRect = textureRect(1, 0)
		val bodyVertRect = textureRect(2, 1)
		val bodyLeftDownRect = textureRect(0, 0)
		val bodyLeftUpRect = textureRect(0, 1)
		val bodyRightDownRect = textureRect(2, 0)
		val bodyRightUpRect = textureRect(2, 2)

		val appleRect = textureRect(0, 3)
		val emptyRect = textureRect(0, 2)

		val grassRect = allocRect(0, 0, 256, 256)

		private fun textureRect(x: Int, y: Int) = allocRect(x * w, y * h, w, h)

		fun render(srcRect: SDL_Rect, dstRect: SDL_Rect) {
			if (srcRect == grassRect) libSDL2Library.SDL_RenderCopy(renderer, grassTexture, srcRect, dstRect)
			else libSDL2Library.SDL_RenderCopy(renderer, texture, srcRect, dstRect)
		}
	}

	override fun close() {
		controller.takeIf { it != null }
			?.let { libSDL2Library.SDL_GameControllerClose(it) }
		removeTexture(sprites.texture)
		removeTexture(sprites.grassTexture)
		removeTexture(font.texture)
	}
}

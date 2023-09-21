package snake


import SDL_WINDOWPOS_CENTERED
import io.github.libsdl4j.api.event.SdlEvents
import libsdl.*
import java.io.File
import kotlin.math.max
import kotlin.random.Random

fun main() {
	val initialGame = Game(
		width = 20,
		height = 10,
		snake = Snake(
			cells = listOf(Cell(4, 4), Cell(3, 4), Cell(2, 4), Cell(1, 4), Cell(0, 4)),
			direction = Direction.right
		)
	)
	var game = initialGame

	SdlUI(game.width, game.height).use { sdlUI ->

		var ticks = 0
		val speed = 10
		while (true) {

			sdlUI.draw(game)

			sdlUI.delay(1000 / 60)
			ticks++
			if (ticks >= speed) {
				game = game.update()
				ticks -= speed
			}

			sdlUI.readCommands2().forEach { command ->
				var direction: Direction? = null
				when (command) {
					SdlUI.UserCommand.up -> direction = Direction.up
					SdlUI.UserCommand.down -> direction = Direction.down
					SdlUI.UserCommand.left -> direction = Direction.left
					SdlUI.UserCommand.right -> direction = Direction.right
					SdlUI.UserCommand.restart -> game = initialGame
					SdlUI.UserCommand.quit -> return
				}
				game = game.update(direction)
				sdlUI.draw(game)
			}
		}
	}
}

class SdlUI(width: Int, height: Int): AutoCloseable {
	private val window: SDL_Window.ByReference
	private val renderer: SDL_Renderer.ByReference
	private val font: Font
	private val sprites: Sprites

	private val pixelWidth = width * Sprites.w
	private val pixelHeight = height * Sprites.h

	init {
		if (libSDL2Library.SDL_Init(SDL_INIT_EVERYTHING) != 0) {
			println("SDL_Init Error: ${libSDL2Library.SDL_GetError()}")
			throw Error()
		}

		window = libSDL2Library.SDL_CreateWindow("Snake", SDL_WINDOWPOS_CENTERED,
			SDL_WINDOWPOS_CENTERED, pixelWidth, pixelHeight,
			SDL_WindowFlags.SDL_WINDOW_SHOWN.value
		)

		renderer = libSDL2Library.SDL_CreateRenderer(
			window, -1, SDL_RendererFlags.SDL_RENDERER_ACCELERATED or SDL_RendererFlags.SDL_RENDERER_PRESENTVSYNC
		)

		font = Font(renderer)
		sprites = Sprites(renderer)
	}

	fun draw(game: Game) {
		libSDL2Library.SDL_RenderClear(renderer)
		libSDL2Library.SDL_SetRenderDrawColor(renderer, (200 / 2).toByte(), (230 / 2).toByte(), (151 / 2).toByte(), SDL_ALPHA_OPAQUE)

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
					Direction.up, Direction.down    -> sprites.bodyVertRect
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
			Direction.up    -> sprites.tipUpRect
			Direction.down  -> sprites.tipDownRect
			Direction.left  -> sprites.tipLeftRect
			Direction.right -> sprites.tipRightRect
		}
		sprites.render(tipRect, cellRect(game.snake.tail.last()))

		val headRect = when (game.snake.direction) {
			Direction.up    -> sprites.headUpRect
			Direction.down  -> sprites.headDownRect
			Direction.left  -> sprites.headLeftRect
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


	fun readCommands(): List<UserCommand>  {
		val result = ArrayList<UserCommand>()
		val event = io.github.libsdl4j.api.event.SDL_Event()
		while (SdlEvents.SDL_PollEvent(event) != 0) {
			event.read()
			when (event.type) {
				io.github.libsdl4j.api.event.SDL_EventType.SDL_QUIT -> result.add(UserCommand.quit)
				io.github.libsdl4j.api.event.SDL_EventType.SDL_KEYDOWN -> {
					val keyboardEvent = event.key
					val keysym = keyboardEvent.keysym
					println("keyboardEvent(${keysym.scancode}): ${keysym.scancode}")
					val command = when (keysym.scancode) {
						io.github.libsdl4j.api.scancode.SDL_Scancode.SDL_SCANCODE_I -> UserCommand.up
						io.github.libsdl4j.api.scancode.SDL_Scancode.SDL_SCANCODE_J -> UserCommand.up
						io.github.libsdl4j.api.scancode.SDL_Scancode.SDL_SCANCODE_K -> UserCommand.up
						io.github.libsdl4j.api.scancode.SDL_Scancode.SDL_SCANCODE_L -> UserCommand.up
						io.github.libsdl4j.api.scancode.SDL_Scancode.SDL_SCANCODE_R -> UserCommand.up
						io.github.libsdl4j.api.scancode.SDL_Scancode.SDL_SCANCODE_Q -> UserCommand.up
						else           -> null
					}
					if (command != null) result.add(command)
				}
				else -> Unit
			}
		}
		return result
	}

	fun readCommands2(): List<UserCommand>  {
		val result = ArrayList<UserCommand>()
		val event = SDL_Event()
		while (libSDL2Library.SDL_PollEvent(event) != 0) {
			event.read()
			println("event(${event.type}): ${SDL_EventType.of(event.type)}")
			when (SDL_EventType.of(event.type)) {
				SDL_EventType.SDL_QUIT -> result.add(UserCommand.quit)
				SDL_EventType.SDL_KEYDOWN -> {
					val keyboardEvent = event.key
					val keysym = keyboardEvent!!.keysym
					println("keyboardEvent(${keysym.scancode}): ${SDL_Scancode.of(keysym.scancode)}")
					val command = when (SDL_Scancode.of(keysym.scancode)) {
						SDL_Scancode.SDL_SCANCODE_I -> UserCommand.up
						SDL_Scancode.SDL_SCANCODE_J -> UserCommand.left
						SDL_Scancode.SDL_SCANCODE_K -> UserCommand.down
						SDL_Scancode.SDL_SCANCODE_L -> UserCommand.right
						SDL_Scancode.SDL_SCANCODE_R -> UserCommand.restart
						SDL_Scancode.SDL_SCANCODE_Q -> UserCommand.quit
						else           -> null
					}
					if (command != null) result.add(command)
				}
				else -> Unit
			}
		}
		return result
	}

	private fun direction(from: Cell, to: Cell): Direction = when {
		from.x == to.x && from.y > to.y -> Direction.up
		from.x == to.x && from.y < to.y -> Direction.down
		from.x > to.x && from.y == to.y -> Direction.left
		from.x < to.x && from.y == to.y -> Direction.right
		else                            -> error("")
	}

	private fun cellRect(cell: Cell): SDL_Rect.ByReference {
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


	class Font(private val renderer: SDL_Renderer.ByReference) {
		companion object {
			const val w = 48
			const val h = 46
		}

		internal val texture = renderer.loadTexture("Font16_42_Normal4_sheet.bmp")
		private val letters: Map<Char, SDL_Rect.ByReference>

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

		fun render(char: Char, cellRect: SDL_Rect.ByReference) {
			val charRect = letters[char.uppercaseChar()] ?: (letters[' '] ?: error(""))
			libSDL2Library.SDL_RenderCopy(renderer, texture, charRect, cellRect)
		}

		private fun textureRect(x: Int, y: Int, wAdjust: Int = 0): SDL_Rect.ByReference {
			val xShift = x * w
			val yShift = y * h
			return allocRect(xShift, yShift, w + wAdjust, h)
		}
	}

	class Sprites(private val renderer: SDL_Renderer.ByReference) {
		companion object {
			const val w = 64
			const val h = 64
		}

		internal val texture = renderer.loadTexture("snake-graphics.bmp")
		internal val grassTexture = renderer.loadTexture("grass.bmp")

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

		fun render(srcRect: SDL_Rect.ByReference, dstRect: SDL_Rect.ByReference) {
			if (srcRect == grassRect) libSDL2Library.SDL_RenderCopy(renderer, grassTexture, srcRect, dstRect)
			else libSDL2Library.SDL_RenderCopy(renderer, texture, srcRect, dstRect)
		}
	}

	companion object {
		fun SDL_Renderer.ByReference.loadTexture(fileName: String): SDL_Texture.ByReference {
			val paths = listOf(fileName, "resources/$fileName", "../resources/$fileName")
			val filePath = paths.find { File(it).canRead() } ?: error("Can't find image file.")

			val bmp = libSDL2Library.SDL_LoadBMP_RW(libSDL2Library.SDL_RWFromFile(filePath, "rb"), 1)

			return libSDL2Library.SDL_CreateTextureFromSurface(this@loadTexture, bmp)
		}

		fun allocRect(x: Int, y: Int, w: Int, h: Int) = SDL_Rect.ByReference().also {
			it.x = x
			it.y = y
			it.w = w
			it.h = h
		}
	}

	override fun close() {
		libSDL2Library.SDL_DestroyTexture(sprites.texture)
		libSDL2Library.SDL_DestroyTexture(sprites.grassTexture)
		libSDL2Library.SDL_DestroyTexture(font.texture)
		libSDL2Library.SDL_DestroyRenderer(renderer)
		libSDL2Library.SDL_DestroyWindow(window)
		libSDL2Library.SDL_Quit()
	}
}

data class Game(
	val width: Int,
	val height: Int,
	val snake: Snake,
	val apples: Apples = Apples(width, height)
) {
	val isOver =
		snake.tail.contains(snake.head) ||
			snake.cells.any { it.x < 0 || it.x >= width || it.y < 0 || it.y >= height }

	val score = snake.cells.size

	fun update(direction: Direction? = null): Game {
		if (isOver) return this
		val (newSnake, newApples) = snake.turn(direction).move().eat(apples.grow())
		return copy(snake = newSnake, apples = newApples)
	}
}

data class Snake(
	val cells: List<Cell>,
	val direction: Direction,
	val eatenApples: Int = 0
) {
	val head = cells.first()
	val tail = cells.subList(1, cells.size)

	fun move(): Snake {
		val newHead = head.move(direction)
		val newTail = if (eatenApples == 0) cells.dropLast(1) else cells
		return copy(
			cells = listOf(newHead) + newTail,
			eatenApples = max(eatenApples - 1, 0)
		)
	}

	fun turn(newDirection: Direction?): Snake {
		if (newDirection == null || newDirection.isOpposite(direction)) return this
		return copy(direction = newDirection)
	}

	fun eat(apples: Apples): Pair<Snake, Apples> {
		if (!apples.cells.contains(head)) return Pair(this, apples)
		return Pair(
			copy(eatenApples = eatenApples + 1),
			apples.copy(cells = apples.cells - head)
		)
	}
}

data class Apples(
	val fieldWidth: Int,
	val fieldHeight: Int,
	val cells: Set<Cell> = emptySet(),
	val growthSpeed: Int = 3,
	val random: Random = Random
) {
	fun grow(): Apples {
		if (random.nextInt(growthSpeed) != 0) return this
		return copy(cells = cells + Cell(random.nextInt(fieldWidth), random.nextInt(fieldHeight)))
	}
}

data class Cell(val x: Int, val y: Int) {
	fun move(direction: Direction) = Cell(x + direction.dx, y + direction.dy)
}

enum class Direction(val dx: Int, val dy: Int) {
	up(0, -1), down(0, 1), left(-1, 0), right(1, 0);

	fun isOpposite(that: Direction) = dx + that.dx == 0 && dy + that.dy == 0
}
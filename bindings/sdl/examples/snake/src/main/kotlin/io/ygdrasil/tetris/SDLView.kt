package io.ygdrasil.tetris

import com.sun.jna.ptr.IntByReference
import io.ygdrasil.libsdl.*

class SDLView(private val width: Int, private val height: Int) : GameFieldVisualizer, UserInput {

    fun error(message: Any): Nothing {
        if (this::renderer.isInitialized) SDL_DestroyRenderer(renderer)
        if (this::window.isInitialized) SDL_DestroyWindow(window)
        println("$message: ${SDL_GetError()}")
        SDL_Quit()
        throw IllegalStateException()
    }

    private val CELL_SIZE = 20
    private val COLORS = 10
    private val CELLS_WIDTH = COLORS * CELL_SIZE
    private val CELLS_HEIGHT = 3 * CELL_SIZE
    private val SYMBOL_SIZE = 21
    private val INFO_MARGIN = 10
    private val MARGIN = 2
    private val BORDER_WIDTH = 18
    private val INFO_SPACE_WIDTH = SYMBOL_SIZE * (2 + 8)
    private val LINES_LABEL_WIDTH = 104
    private val SCORE_LABEL_WIDTH = 107
    private val LEVEL_LABEL_WIDTH = 103
    private val NEXT_LABEL_WIDTH = 85
    private val TETRISES_LABEL_WIDTH = 162

    private var ratio: Float

    private fun stretch(value: Int) = (value.toFloat() * ratio + 0.5).toInt()

    inner class GamePadButtons(width: Int, height: Int, gamePadHeight: Int) {
        val MOVE_BUTTON_SIZE = 50
        val ROTATE_BUTTON_SIZE = 80
        val BUTTONS_MARGIN = 25

        val leftRect: SDL_Rect
        val rightRect: SDL_Rect
        val downRect: SDL_Rect
        val dropRect: SDL_Rect
        val rotateRect: SDL_Rect

        init {
            val moveButtonsWidth = 3 * MOVE_BUTTON_SIZE + 2 * BUTTONS_MARGIN + BUTTONS_MARGIN
            val x = (width - moveButtonsWidth - ROTATE_BUTTON_SIZE) / 2 - MOVE_BUTTON_SIZE
            val y2 = (gamePadHeight - 2 * MOVE_BUTTON_SIZE - BUTTONS_MARGIN) / 2
            leftRect = SDL_Rect()
            leftRect.w = MOVE_BUTTON_SIZE
            leftRect.h = MOVE_BUTTON_SIZE
            leftRect.x = x
            leftRect.y = height - gamePadHeight + y2 + MOVE_BUTTON_SIZE + BUTTONS_MARGIN

            downRect = SDL_Rect()
            downRect.w = MOVE_BUTTON_SIZE
            downRect.h = MOVE_BUTTON_SIZE
            downRect.x = x + MOVE_BUTTON_SIZE + BUTTONS_MARGIN
            downRect.y = leftRect.y

            dropRect = SDL_Rect().apply {
				this.x = MOVE_BUTTON_SIZE
				y = MOVE_BUTTON_SIZE
				h = downRect.x
				w =height - gamePadHeight + y2
			}

            rightRect = SDL_Rect()
            rightRect.w = MOVE_BUTTON_SIZE
            rightRect.h = MOVE_BUTTON_SIZE
            rightRect.x = x + 2 * MOVE_BUTTON_SIZE + 2 * BUTTONS_MARGIN
            rightRect.y = height - gamePadHeight + y2 + MOVE_BUTTON_SIZE + BUTTONS_MARGIN

            rotateRect = SDL_Rect()
            rotateRect.w = ROTATE_BUTTON_SIZE
            rotateRect.h = ROTATE_BUTTON_SIZE
            rotateRect.x = x + moveButtonsWidth
            rotateRect.y = height - gamePadHeight + y2 - BUTTONS_MARGIN
        }

        fun getCommandAt(x: Int, y: Int): UserCommand? {
            return when {
                inside(leftRect, x, y) -> UserCommand.LEFT
                inside(rightRect, x, y) -> UserCommand.RIGHT
                inside(downRect, x, y) -> UserCommand.DOWN
                inside(dropRect, x, y) -> UserCommand.DROP
                inside(rotateRect, x, y) -> UserCommand.ROTATE
                else -> null
            }
        }

        private fun inside(rect: SDL_Rect, x: Int, y: Int): Boolean {
            return x >= stretch(rect.x) && x <= stretch(rect.x + rect.w)
                    && y >= stretch(rect.y) && y <= stretch(rect.y + rect.h)
        }

    }

    private val field: Field = Array(height) { ByteArray(width) }
    private val nextPieceField: Field = Array(4) { ByteArray(4) }
    private var linesCleared: Int = 0
    private var level: Int = 0
    private var score: Int = 0
    private var tetrises: Int = 0

    private var displayWidth: Int = 0
    private var displayHeight: Int = 0
    private val fieldWidth: Int
    private val fieldHeight: Int
    private var windowX: Int
    private var windowY: Int
    private lateinit var window: SDL_Window
    private lateinit var renderer: SDL_Renderer
    private val texture: SDL_Texture
    private val gamePadButtons: GamePadButtons?

    init {
        if (SDL_Init(SDL_INIT_EVERYTHING.toInt()) != 0) {
            throw Error("SDL_Init Error: ${SDL_GetError()}")
        }

        val platform: String = SDL_GetPlatform() ?: error("fail to get platform")

		val displayMode = SDL_DisplayMode()
        if (SDL_GetCurrentDisplayMode(0, displayMode) != 0) {
            println("SDL_GetCurrentDisplayMode Error: ${SDL_GetError()}")
            SDL_Quit()
            throw Error()
        }
        displayWidth = displayMode.w
        displayHeight = displayMode.h
        fieldWidth = width * (CELL_SIZE + MARGIN) + MARGIN + BORDER_WIDTH * 2
        fieldHeight = height * (CELL_SIZE + MARGIN) + MARGIN + BORDER_WIDTH * 2
        var windowWidth = fieldWidth + INFO_SPACE_WIDTH
        var windowHeight: Int
        if (platform == "iOS") {
            val gamePadHeight = (displayHeight * windowWidth - fieldHeight * displayWidth) / displayWidth
            windowHeight = fieldHeight + gamePadHeight
            gamePadButtons = GamePadButtons(windowWidth, windowHeight, gamePadHeight)
            windowX = 0
            windowY = 0
            ratio = displayHeight.toFloat() / windowHeight
            windowWidth = displayWidth
            windowHeight = displayHeight
        } else {
            windowHeight = fieldHeight
            gamePadButtons = null
            windowX = (displayWidth - windowWidth) / 2
            windowY = (displayHeight - windowHeight) / 2
            ratio = 1.0f
        }

        window = SDL_CreateWindow(
            "Tetris", windowX, windowY, windowWidth, windowHeight,
            SDL_WindowFlags.SDL_WINDOW_SHOWN or SDL_WindowFlags.SDL_WINDOW_ALLOW_HIGHDPI
        ) ?: error("")

        renderer = SDL_CreateRenderer(window, -1, SDL_RendererFlags.SDL_RENDERER_ACCELERATED or SDL_RendererFlags.SDL_RENDERER_PRESENTVSYNC)
			?: error("")

		val realWidth = IntByReference()
		val realHeight = IntByReference()
        SDL_GetRendererOutputSize(renderer, realWidth.pointer, realHeight.pointer)
        if (platform != "iOS" && windowHeight != realHeight.value) {
            println("DPI differs ${realWidth} x ${realHeight} vs $windowWidth x $windowHeight")
            ratio = realHeight.value.toFloat() / windowHeight
        }

        texture = loadImage(renderer, "tetris_all.bmp")
    }

    private fun loadImage(ren: SDL_Renderer, imagePath: String): SDL_Texture {
        val bmp = SDL_LoadBMP_RW(SDL_RWFromFile(imagePath, "rb"), 1)

        val tex = SDL_CreateTextureFromSurface(ren, bmp) ?: error("")
        SDL_FreeSurface(bmp)
        return tex
    }

    override fun drawCell(x: Int, y: Int, cell: Byte) {
        field[x][y] = cell
    }

    override fun drawNextPieceCell(x: Int, y: Int, cell: Byte) {
        nextPieceField[x][y] = cell
    }

    override fun setInfo(linesCleared: Int, level: Int, score: Int, tetrises: Int) {
        this.linesCleared = linesCleared
        this.level = level
        this.score = score
        this.tetrises = tetrises
    }

    override fun refresh() {
        SDL_RenderClear(renderer)
        drawField()
        drawInfo()
        drawNextPiece()
        drawGamePad()
        SDL_RenderPresent(renderer)
    }

    private fun drawBorder(topLeftX: Int, topLeftY: Int, width: Int, height: Int) {
        // Upper-left corner.
        var srcX = CELLS_WIDTH
        var srcY = 0
        var destX = topLeftX
        var destY = topLeftY
        copyRect(srcX, srcY, destX, destY, BORDER_WIDTH + MARGIN, BORDER_WIDTH)

        // Upper margin.
        srcX += BORDER_WIDTH + MARGIN
        destX += BORDER_WIDTH + MARGIN
        for (i in 0..width - 1) {
            copyRect(srcX, srcY, destX, destY, CELL_SIZE + MARGIN, BORDER_WIDTH)
            destX += CELL_SIZE + MARGIN
        }

        // Upper-right corner.
        srcX += CELL_SIZE + MARGIN
        copyRect(srcX, srcY, destX, destY, BORDER_WIDTH, BORDER_WIDTH + MARGIN)

        // Right margin.
        srcY += BORDER_WIDTH + MARGIN
        destY += BORDER_WIDTH + MARGIN
        for (j in 0..height - 1) {
            copyRect(srcX, srcY, destX, destY, BORDER_WIDTH, CELL_SIZE + MARGIN)
            destY += CELL_SIZE + MARGIN
        }

        // Left margin.
        srcX = CELLS_WIDTH
        srcY = BORDER_WIDTH
        destX = topLeftX
        destY = topLeftY + BORDER_WIDTH
        for (j in 0..height - 1) {
            copyRect(srcX, srcY, destX, destY, BORDER_WIDTH, CELL_SIZE + MARGIN)
            destY += CELL_SIZE + MARGIN
        }

        // Left-down corner.
        srcY += CELL_SIZE + MARGIN
        copyRect(srcX, srcY, destX, destY, BORDER_WIDTH, BORDER_WIDTH + MARGIN)

        // Down marign.
        srcX += BORDER_WIDTH
        srcY += MARGIN
        destX += BORDER_WIDTH
        destY += MARGIN
        for (i in 0..width - 1) {
            copyRect(srcX, srcY, destX, destY, CELL_SIZE + MARGIN, BORDER_WIDTH)
            destX += CELL_SIZE + MARGIN

        }
        // Right-down corner.
        srcX += CELL_SIZE + MARGIN
        copyRect(srcX, srcY, destX, destY, BORDER_WIDTH + MARGIN, BORDER_WIDTH)
    }

    private fun drawField() {
        drawField(
            field = field,
            topLeftX = 0,
            topLeftY = 0,
            width = width,
            height = height
        )
    }

    private fun drawNextPiece() {
        drawInt(
            labelSrcX = LEVEL_LABEL_WIDTH,
            labelSrcY = CELLS_HEIGHT + SYMBOL_SIZE,
            labelDestX = fieldWidth + SYMBOL_SIZE,
            labelDestY = getInfoY(5),
            labelWidth = NEXT_LABEL_WIDTH,
            totalDigits = 0,
            value = 0
        )
        drawField(
            field = nextPieceField,
            topLeftX = fieldWidth + SYMBOL_SIZE,
            topLeftY = getInfoY(6),
            width = 4,
            height = 4
        )
    }

    private fun drawField(field: Field, topLeftX: Int, topLeftY: Int, width: Int, height: Int) {
        drawBorder(
            topLeftX = topLeftX,
            topLeftY = topLeftY,
            width = width,
            height = height
        )
        for (i in 0..height - 1)
            for (j in 0..width - 1) {
                val cell = field[i][j].toInt()
                if (cell == 0) continue
                copyRect(
                    srcX = (level % COLORS) * CELL_SIZE,
                    srcY = (3 - cell) * CELL_SIZE,
                    destX = topLeftX + BORDER_WIDTH + MARGIN + j * (CELL_SIZE + MARGIN),
                    destY = topLeftY + BORDER_WIDTH + MARGIN + i * (CELL_SIZE + MARGIN),
                    width = CELL_SIZE,
                    height = CELL_SIZE
                )
            }
    }

    private fun drawInfo() {
        drawInt(
            labelSrcX = LINES_LABEL_WIDTH,
            labelSrcY = CELLS_HEIGHT,
            labelDestX = fieldWidth + SYMBOL_SIZE,
            labelDestY = getInfoY(0),
            labelWidth = SCORE_LABEL_WIDTH,
            totalDigits = 6,
            value = score
        )
        drawInt(
            labelSrcX = 0,
            labelSrcY = CELLS_HEIGHT,
            labelDestX = fieldWidth + SYMBOL_SIZE,
            labelDestY = getInfoY(1),
            labelWidth = LINES_LABEL_WIDTH,
            totalDigits = 3,
            value = linesCleared
        )
        drawInt(
            labelSrcX = 0,
            labelSrcY = CELLS_HEIGHT + SYMBOL_SIZE,
            labelDestX = fieldWidth + SYMBOL_SIZE,
            labelDestY = getInfoY(2),
            labelWidth = LEVEL_LABEL_WIDTH,
            totalDigits = 2,
            value = level
        )
        drawInt(
            labelSrcX = 0,
            labelSrcY = CELLS_HEIGHT + SYMBOL_SIZE * 2,
            labelDestX = fieldWidth + SYMBOL_SIZE,
            labelDestY = getInfoY(3),
            labelWidth = TETRISES_LABEL_WIDTH,
            totalDigits = 2,
            value = tetrises
        )
    }

    private fun getInfoY(line: Int): Int {
        return SYMBOL_SIZE * (2 * line + 1) + INFO_MARGIN * line
    }

    private fun drawInt(
        labelSrcX: Int, labelSrcY: Int, labelDestX: Int, labelDestY: Int,
        labelWidth: Int, totalDigits: Int, value: Int
    ) {
        copyRect(
            srcX = labelSrcX,
            srcY = labelSrcY,
            destX = labelDestX,
            destY = labelDestY,
            width = labelWidth,
            height = SYMBOL_SIZE
        )
        val digits = IntArray(totalDigits)
        var x = value
        for (i in 0..totalDigits - 1) {
            digits[totalDigits - 1 - i] = x % 10
            x = x / 10
        }
        for (i in 0..totalDigits - 1) {
            copyRect(
                srcX = digits[i] * SYMBOL_SIZE,
                srcY = CELLS_HEIGHT + 3 * SYMBOL_SIZE,
                destX = labelDestX + SYMBOL_SIZE + i * SYMBOL_SIZE,
                destY = labelDestY + SYMBOL_SIZE,
                width = SYMBOL_SIZE,
                height = SYMBOL_SIZE
            )
        }
    }

    private fun drawGamePad() {
        if (gamePadButtons == null) return
        SDL_SetRenderDrawColor(renderer, 127, 127, 127, SDL_ALPHA_OPAQUE.toByte())
        fillRect(gamePadButtons.leftRect)
        fillRect(gamePadButtons.downRect)
        fillRect(gamePadButtons.dropRect)
        fillRect(gamePadButtons.rightRect)
        fillRect(gamePadButtons.rotateRect)
        SDL_SetRenderDrawColor(renderer, 0, 0, 0, SDL_ALPHA_OPAQUE.toByte())
    }

    private fun fillRect(rect: SDL_Rect) {
        val stretchedRect = SDL_Rect().apply {
            w = stretch(rect.w)
            h = stretch(rect.h)
            x = stretch(rect.x)
            y = stretch(rect.y)
        }
        SDL_RenderFillRect(renderer, stretchedRect)
    }


    private fun copyRect(srcX: Int, srcY: Int, destX: Int, destY: Int, width: Int, height: Int) {
        val srcRect = SDL_Rect().apply {
            w = width
            h = height
            x = srcX
            y = srcY
        }
        val destRect = SDL_Rect().apply {
            w = stretch(width)
            h = stretch(height)
            x = stretch(destX)
            y = stretch(destY)
        }
        SDL_RenderCopy(renderer, texture, srcRect, destRect)
    }

    override fun readCommands(): List<UserCommand> {
        val commands = mutableListOf<UserCommand>()
		val event = SDL_Event()
        SDL_PollEvent(event)
        while (event != null) {
            when (SDL_EventType.of(event.type)) {
                SDL_EventType.SDL_QUIT -> commands.add(UserCommand.EXIT)
                SDL_EventType.SDL_KEYDOWN -> {
					val keyboardEvent = event.key
                    when (keyboardEvent.keysym.scancode.toInt()) {
                        SDL_Scancode.SDL_SCANCODE_LEFT.value -> commands.add(UserCommand.LEFT)
						SDL_Scancode.SDL_SCANCODE_RIGHT.value -> commands.add(UserCommand.RIGHT)
						SDL_Scancode.SDL_SCANCODE_DOWN.value -> commands.add(UserCommand.DOWN)
						SDL_Scancode.SDL_SCANCODE_Z.value, SDL_Scancode.SDL_SCANCODE_SPACE.value -> commands.add(UserCommand.ROTATE)
						SDL_Scancode.SDL_SCANCODE_UP.value -> commands.add(UserCommand.DROP)
						SDL_Scancode.SDL_SCANCODE_ESCAPE.value -> commands.add(UserCommand.EXIT)
                    }
                }
                SDL_EventType.SDL_MOUSEBUTTONDOWN -> if (gamePadButtons != null) {
                    val mouseEvent = event as SDL_MouseButtonEvent
                    val x = mouseEvent.x
                    val y = mouseEvent.y
                    val command = gamePadButtons.getCommandAt(x, y)
                    if (command != null)
                        commands.add(command)
                }

				else -> { }
			}

			SDL_PollEvent(event)
        }
        return commands
    }

    fun destroy() {
		SDL_DestroyTexture(texture)
		SDL_DestroyRenderer(renderer)
		SDL_DestroyWindow(window)
		SDL_Quit()
    }
}

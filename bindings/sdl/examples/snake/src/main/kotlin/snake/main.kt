package snake

import sdl.app

fun main() = app {

	SnakeView(this, initialGameState.width, initialGameState.height).use { view ->

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
					SnakeView.UserCommand.quit -> return@app
				}
				game = game.update(direction)
				view.draw(game)
			}
		}
	}
}

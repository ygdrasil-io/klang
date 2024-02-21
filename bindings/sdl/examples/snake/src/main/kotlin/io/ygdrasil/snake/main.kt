package io.ygdrasil.snake

import io.ygdrasil.sdl.app

fun main() = app {

	SnakeView(this).use { view ->

		while (true) {

			view.draw(view.game)

			view.delay(1000 / 60)
			view.ticks++
			if (view.ticks >= view.speed) {
				view.game = view.game.update()
				view.ticks = 0
			}

			view.readCommands().forEach { command ->
				var direction: Direction? = null
				when (command) {
					SnakeView.UserCommand.up -> direction = Direction.up
					SnakeView.UserCommand.down -> direction = Direction.down
					SnakeView.UserCommand.left -> direction = Direction.left
					SnakeView.UserCommand.right -> direction = Direction.right
					SnakeView.UserCommand.restart -> view.game = initialGameState
					SnakeView.UserCommand.quit -> return@app
				}
				view.game = view.game.update(direction)
				view.draw(view.game)
			}
		}
	}
}

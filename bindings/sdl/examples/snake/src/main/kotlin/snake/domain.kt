package snake

import kotlin.math.max
import kotlin.random.Random

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

val initialGameState = Game(
	width = 20,
	height = 10,
	snake = Snake(
		cells = listOf(Cell(4, 4), Cell(3, 4), Cell(2, 4), Cell(1, 4), Cell(0, 4)),
		direction = Direction.right
	)
)

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
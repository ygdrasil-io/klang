package tetris


fun main() {

    val visualizer = SDLView(10, 20)
    val game = Game(10, 20, visualizer, visualizer)
    game.startNewGame()

    return
}
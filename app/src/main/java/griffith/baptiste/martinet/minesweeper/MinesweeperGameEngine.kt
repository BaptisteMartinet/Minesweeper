package griffith.baptiste.martinet.minesweeper

class MinesweeperGameEngine(private val boardSize: Int) {
  class Cell {
    private var _value: Int = 0
    private var _isFlagged: Boolean = false
    private var _isRevealed: Boolean = false
  }

  private var board: List<List<Cell>> = List(boardSize) { List(boardSize) { Cell() } }
}
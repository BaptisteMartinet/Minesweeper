package griffith.baptiste.martinet.minesweeper

import android.graphics.Point
import kotlin.random.Random

class MinesweeperGameEngine(private val _boardSize: Int, private val _nbMines: Int) {
  private var _board: List<List<Cell>> = List(_boardSize) { List(_boardSize) { Cell() } }

  init {
    generateBoard()
  }

  private fun generateBoard() {
    placeMines()
    fillCellsValues()
  }

  private fun placeMines() {
    var cell: Cell
    for (i in 0 until _nbMines) {
      do {
        val mineCoordinates = Point(Random.nextInt(0, _boardSize), Random.nextInt(0, _boardSize))
        cell = getCellAtPos(mineCoordinates.x, mineCoordinates.y)
      } while (cell.getValue() == -1)
      cell.setValue(-1)
    }
  }

  private fun fillCellsValues() {
    for (y in _board.indices) {
      for (x in _board[y].indices) {
        if (getCellAtPos(x, y).getValue() == -1)
          continue
        _board[y][x].setValue(getCellMinesNeighborCount(x, y))
      }
    }
  }

  private fun isPosInBounds(x: Int, y: Int): Boolean = (x in 0 until _boardSize && y in 0 until _boardSize)

  fun getCellAtPos(x: Int, y: Int): Cell = _board[y][x]

  private fun getCellMinesNeighborCount(x: Int, y: Int): Int {
    var count = 0
    for (j in y - 1 until y + 2) {
      for (i in x - 1 until x + 2) {
        if (!isPosInBounds(i, j))
          continue
        if (j == y && i == x)
          continue
        if (getCellAtPos(i, j).getValue() == -1)
          count++
      }
    }
    return count
  }
}
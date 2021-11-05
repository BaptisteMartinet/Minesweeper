package griffith.baptiste.martinet.minesweeper

import android.graphics.Point
import kotlin.random.Random

class MinesweeperGameEngine(private val _boardSize: Int, private var _nbMines: Int) {
  enum class StatesEnum {
    PLAYING,
    FINISHED,
  }

  private lateinit var _board: List<List<Cell>>
  private var _gameState = StatesEnum.PLAYING

  init {
    _nbMines.coerceIn(0, _boardSize*_boardSize)
    generateBoard()
  }

  fun getGameState(): StatesEnum = _gameState

  private fun setGameState(state: StatesEnum) { _gameState = state }

  fun reset() {
    _gameState = StatesEnum.PLAYING
    generateBoard()
  }

  private fun generateBoard() {
    _board = List(_boardSize) { List(_boardSize) { Cell() } }
    placeMines()
    fillCellsValues()
  }

  private fun finishGame() {
    setGameState(StatesEnum.FINISHED)
    revealAllMines()
  }

  private fun placeMines() {
    var cell: Cell
    for (i in 0 until _nbMines) {
      do {
        val mineCoordinates = Point(Random.nextInt(0, _boardSize), Random.nextInt(0, _boardSize))
        cell = getCellAtPos(mineCoordinates.x, mineCoordinates.y)!!
      } while (cell.getValue() == -1)
      cell.setMined()
    }
  }

  private fun fillCellsValues() {
    for (y in _board.indices) {
      for (x in _board[y].indices) {
        if (getCellAtPos(x, y)!!.isMine())
          continue
        _board[y][x].setValue(getCellMinesNeighborCount(x, y))
      }
    }
  }

  private fun isPosInBounds(x: Int, y: Int): Boolean = (x in 0 until _boardSize && y in 0 until _boardSize)

  fun getCellAtPos(x: Int, y: Int): Cell? {
    if (!isPosInBounds(x, y))
      return null
    return _board[y][x]
  }

  private fun getCellMinesNeighborCount(x: Int, y: Int): Int {
    var count = 0
    for (j in y - 1 until y + 2) {
      for (i in x - 1 until x + 2) {
        if (j == y && i == x)
          continue
        if (getCellAtPos(i, j)?.isMine() == true)
          count++
      }
    }
    return count
  }

  fun revealCellAtPos(x: Int, y: Int, calledByUser: Boolean) {
    val cell = getCellAtPos(x, y) ?: return
    if (calledByUser && cell.isFlagged())
      return
    if (cell.isRevealed())
      return
    if (!calledByUser && cell.isMine())
      return
    cell.setFlagged(false)
    cell.setRevealed()
    if (cell.isMine()) {
      finishGame()
      return
    }
    if (cell.getValue() != 0) {
      return
    }
    for (j in y - 1 until y + 2) {
      for (i in x - 1 until x + 2) {
        if (j == y && i == x)
          continue
        revealCellAtPos(i, j, false)
      }
    }
    return
  }

  fun flagCellAtPos(x: Int, y: Int): Boolean {
    val cell = getCellAtPos(x, y) ?: return false
    if (cell.isRevealed())
      return false
    val isCellFlagged = cell.isFlagged()
    if (!isCellFlagged && getRemainingFlagsCount() <= 0)
      return false
    cell.setFlagged(!isCellFlagged)
    return true
  }

  private fun revealAllMines() {
    for (y in _board.indices) {
      for (x in _board[y].indices) {
        if (_board[y][x].isMine())
          _board[y][x].setRevealed()
      }
    }
  }

  fun getRemainingFlagsCount(): Int = _nbMines - getFlaggedCellCount()

  private fun getFlaggedCellCount(): Int {
    var count = 0
    for (y in _board.indices) {
      for (x in _board[y].indices) {
        if (_board[y][x].isFlagged())
          count++
      }
    }
    return count
  }
}
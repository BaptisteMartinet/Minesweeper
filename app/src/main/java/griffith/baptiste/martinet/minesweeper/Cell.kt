package griffith.baptiste.martinet.minesweeper

class Cell {
  private var _value: Int = 0
  private var _isFlagged: Boolean = false
  private var _isRevealed: Boolean = false

  fun setValue(value: Int) { _value = value }
  fun getValue(): Int = _value

  fun setFlagged(flagged: Boolean) { _isFlagged = flagged }
  fun getFlagged(): Boolean = _isFlagged

  fun setRevealed(revealed: Boolean) { _isRevealed = revealed }
  fun getRevealed(): Boolean = _isRevealed
}

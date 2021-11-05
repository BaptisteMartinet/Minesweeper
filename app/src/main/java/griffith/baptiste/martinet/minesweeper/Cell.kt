package griffith.baptiste.martinet.minesweeper

class Cell {
  private var _value: Int = 0
  private var _isFlagged: Boolean = false
  private var _isRevealed: Boolean = false
  private var _isMine: Boolean = false

  fun setValue(value: Int) { _value = value }
  fun getValue(): Int = _value

  fun setFlagged(flagged: Boolean) { _isFlagged = flagged }
  fun isFlagged(): Boolean = _isFlagged

  fun setRevealed() { _isRevealed = true }
  fun isRevealed(): Boolean = _isRevealed

  fun setMined() { _isMine = true }
  fun isMine(): Boolean = _isMine
}

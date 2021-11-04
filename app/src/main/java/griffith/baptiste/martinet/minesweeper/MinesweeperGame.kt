package griffith.baptiste.martinet.minesweeper

import android.content.Context
import android.util.AttributeSet
import android.view.View

class MinesweeperGame(context: Context, attrs: AttributeSet) : View(context, attrs) {
  private var _boardSize: Int
  init {
    context.theme.obtainStyledAttributes(attrs, R.styleable.MinesweeperGame, 0, 0).apply {
      try {
        _boardSize = getInteger(R.styleable.MinesweeperGame_boardSize, 10)
      } finally {
        recycle()
      }
    }
  }

  fun getBoardSize(): Int = _boardSize

  fun setBoardSize(boardSize: Int) {
    _boardSize = boardSize
    invalidate() //Tells android that the view needs to be redrawn
    requestLayout() //Tells android that the view might have changed in shape or size
  }
}
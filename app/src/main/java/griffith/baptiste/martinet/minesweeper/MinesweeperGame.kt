package griffith.baptiste.martinet.minesweeper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class MinesweeperGame(context: Context, attrs: AttributeSet) : View(context, attrs) {
  private var _boardSize: Int
  private var _cellSize: Float = 0f
  private var _padding: Float = 15f

  private val _paintCellBackground: Paint = Paint()
  private val _paintLineColor: Paint = Paint()

  init {
    context.theme.obtainStyledAttributes(attrs, R.styleable.MinesweeperGame, 0, 0).apply {
      try {
        _boardSize = getInteger(R.styleable.MinesweeperGame_boardSize, 10)
      } finally {
        recycle()
      }
    }
    _paintCellBackground.color = Color.parseColor("#242424")

    _paintLineColor.color = Color.parseColor("#ffffff")
    _paintLineColor.style = Paint.Style.STROKE
    _paintLineColor.strokeWidth = _padding
  }

  fun getBoardSize(): Int = _boardSize

  fun setBoardSize(boardSize: Int) {
    _boardSize = boardSize
    invalidate() //Tells android that the view needs to be redrawn
    requestLayout() //Tells android that the view might have changed in shape or size
  }

  fun drawBoard(canvas: Canvas) {
    for (y in 0 until _boardSize) {
      for (x in 0 until _boardSize) {
        val rect = RectF(x * _cellSize, y * _cellSize, x * _cellSize + _cellSize, y * _cellSize + _cellSize)
        canvas.drawRect(rect, _paintCellBackground)
        canvas.drawRect(rect, _paintLineColor)
      }
    }
    /*for (y in 0 until _boardSize) {
      canvas.drawRect(0f, y * _cellSize - _padding/2, width.toFloat(), y * _cellSize + _padding/2, _paintLineColor)
    }
    for (x in 0 until _boardSize) {
      canvas.drawRect(x * _cellSize - _padding/2, 0f, x * _cellSize + _padding/2, width.toFloat(), _paintLineColor)
    }*/
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawBoard(canvas)
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    Log.i("APP", "View touched!")
    return super.onTouchEvent(event)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    _cellSize = width / _boardSize.toFloat()
  }
}
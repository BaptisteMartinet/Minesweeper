package griffith.baptiste.martinet.minesweeper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class MinesweeperGame(context: Context, attrs: AttributeSet) : View(context, attrs) {
  private var _boardSize: Int
  private var _nbMines: Int
  private var _cellSize: Float = 0f

  private val _paintCellBackground: Paint = Paint()
  private val _paintCellStroke: Paint = Paint()
  private val _paintTextValue: Paint = Paint()

  private var _minesweeperEngine: MinesweeperGameEngine

  init {
    context.theme.obtainStyledAttributes(attrs, R.styleable.MinesweeperGame, 0, 0).apply {
      try {
        _boardSize = getInteger(R.styleable.MinesweeperGame_boardSize, 10)
        _nbMines = getInteger(R.styleable.MinesweeperGame_nbMines, 20)
        _paintCellBackground.color = getColor(R.styleable.MinesweeperGame_cellBackgroundColor, ContextCompat.getColor(context, R.color.black))
        _paintCellStroke.color = getColor(R.styleable.MinesweeperGame_cellStrokeColor, ContextCompat.getColor(context, R.color.white))
        _paintCellStroke.strokeWidth = getFloat(R.styleable.MinesweeperGame_cellPadding, 5f)
      } finally {
        recycle()
      }
    }
    _paintCellStroke.style = Paint.Style.STROKE

    _paintTextValue.color = Color.parseColor("#0062ff")
    _paintTextValue.textAlign = Paint.Align.CENTER
    _paintTextValue.textSize = 50f

    _minesweeperEngine = MinesweeperGameEngine(_boardSize, _nbMines)
  }

  private fun drawCell(x: Int, y: Int, canvas: Canvas) {
    val cell = _minesweeperEngine.getCellAtPos(x, y)
    val topLeft = PointF(x * _cellSize, y * _cellSize)
    val bottomRight = PointF(x * _cellSize + _cellSize, y * _cellSize + _cellSize)
    //Draw cell
    val rect = RectF(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y)
    canvas.drawRect(rect, _paintCellBackground)
    canvas.drawRect(rect, _paintCellStroke)

    canvas.drawText(cell.getValue().toString(), topLeft.x + _cellSize/2, topLeft.y + _cellSize/2, _paintTextValue)
  }

  private fun drawBoard(canvas: Canvas) {
    for (y in 0 until _boardSize) {
      for (x in 0 until _boardSize) {
        drawCell(x, y, canvas)
      }
    }
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
    invalidate()
    requestLayout()
  }
}
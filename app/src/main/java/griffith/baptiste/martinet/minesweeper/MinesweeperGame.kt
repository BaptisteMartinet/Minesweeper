package griffith.baptiste.martinet.minesweeper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlin.math.floor

class MinesweeperGame(context: Context, attrs: AttributeSet) : View(context, attrs) {
  private var _boardSize: Int
  private var _nbMines: Int
  private var _cellSize: Float = 0f

  private val _paintCellBackground: Paint = Paint()
  private val _paintCellBackgroundRevealed: Paint = Paint()
  private val _paintCellStroke: Paint = Paint()
  private val _paintCellValue: Paint = TextPaint()

  private var _minesweeperEngine: MinesweeperGameEngine

  init {
    context.theme.obtainStyledAttributes(attrs, R.styleable.MinesweeperGame, 0, 0).apply {
      try {
        _boardSize = getInteger(R.styleable.MinesweeperGame_boardSize, 10)
        _nbMines = getInteger(R.styleable.MinesweeperGame_nbMines, 20)
        _paintCellBackground.color = getColor(
          R.styleable.MinesweeperGame_cellBackgroundColor,
          ContextCompat.getColor(context, R.color.black)
        )
        _paintCellBackgroundRevealed.color = getColor(
          R.styleable.MinesweeperGame_cellBackgroundColorRevealed,
          ContextCompat.getColor(context, R.color.purple_200)
        )
        _paintCellStroke.color = getColor(
          R.styleable.MinesweeperGame_cellStrokeColor,
          ContextCompat.getColor(context, R.color.white)
        )
        _paintCellStroke.strokeWidth = getFloat(R.styleable.MinesweeperGame_cellPadding, 5f)
        _paintCellValue.color = getColor(
          R.styleable.MinesweeperGame_cellValueColor,
          ContextCompat.getColor(context, R.color.white)
        )
      } finally {
        recycle()
      }
    }
    _paintCellStroke.style = Paint.Style.STROKE
    _paintCellValue.textAlign = Paint.Align.CENTER

    _minesweeperEngine = MinesweeperGameEngine(_boardSize, _nbMines)
  }

  fun reset() {
    _minesweeperEngine.reset()
    invalidate()
  }

  private fun drawCell(x: Int, y: Int, canvas: Canvas) {
    val cell = _minesweeperEngine.getCellAtPos(x, y)
    val topLeft = PointF(x * _cellSize, y * _cellSize)
    val bottomRight = PointF(x * _cellSize + _cellSize, y * _cellSize + _cellSize)
    val center = PointF(topLeft.x + _cellSize / 2, topLeft.y + _cellSize / 2)

    val rect = RectF(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y)

    if (!cell.getRevealed()) {
      canvas.drawRect(rect, _paintCellBackground)
    } else {
      val cellValue = cell.getValue()
      var text = cellValue.toString()
      if (cellValue == -1) {
        text = "M"
      }
      canvas.drawRect(rect, _paintCellBackgroundRevealed)
      canvas.drawText(text, center.x, center.y, _paintCellValue)
    }
    canvas.drawRect(rect, _paintCellStroke)
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

  private fun manageBoardClick(touchX: Float, touchY: Float) {
    if (_minesweeperEngine.getGameState() == MinesweeperGameEngine.StatesEnum.FINISHED) {
      Toast.makeText(context, "Game is already finished", Toast.LENGTH_SHORT).show()
      return
    }
    val pos = Point(
      floor(touchX / _cellSize).toInt(),
      floor(touchY / _cellSize).toInt()
    )
    if (!_minesweeperEngine.isPosInBounds(pos.x, pos.y))
      return
    val cell = _minesweeperEngine.getCellAtPos(pos.x, pos.y)
    cell.setRevealed(true)
    if (cell.getValue() == -1) {
      _minesweeperEngine.setGameState(MinesweeperGameEngine.StatesEnum.FINISHED)
      Toast.makeText(context, "Game terminated", Toast.LENGTH_LONG).show()
    }
    invalidate()
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    manageBoardClick(event.x, event.y)
    return super.onTouchEvent(event)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    _cellSize = width / _boardSize.toFloat()
    _paintCellValue.textSize = _cellSize / 2
    invalidate()
    requestLayout()
  }
}
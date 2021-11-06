package griffith.baptiste.martinet.minesweeper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.CountDownTimer
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.floor
import kotlin.math.min

class MinesweeperGame(context: Context, attrs: AttributeSet) : View(context, attrs) {
  enum class ModeEnum {
    UNCOVERING,
    FLAGGING,
  }

  lateinit var remainingFlagsTextView: TextView

  private var _boardSize: Int
  private var _nbMines: Int
  private var _cellSize: Float = 0f
  private var _mode = ModeEnum.UNCOVERING
  private lateinit var _timer: Timer
  private var _isTimerRunning: Boolean = false

  private val _paintCellBackground: List<Paint> = List(2) { Paint() }
  private val _paintCellBackgroundRevealed: List<Paint> = List(2) { Paint() }
  private val _paintCellBackgroundMine: Paint = Paint()
  private val _paintCellValue: Paint = TextPaint()

  private var _minesweeperEngine: MinesweeperGameEngine

  init {
    context.theme.obtainStyledAttributes(attrs, R.styleable.MinesweeperGame, 0, 0).apply {
      try {
        _boardSize = getInteger(R.styleable.MinesweeperGame_boardSize, 10)
        _nbMines = getInteger(R.styleable.MinesweeperGame_nbMines, 20)

        _paintCellBackground[0].color = getColor(R.styleable.MinesweeperGame_cellBackgroundColor, ContextCompat.getColor(context, R.color.black))
        _paintCellBackground[1].color = getColor(R.styleable.MinesweeperGame_cellBackgroundColor2, ContextCompat.getColor(context, R.color.black))
        _paintCellBackgroundRevealed[0].color = getColor(R.styleable.MinesweeperGame_cellBackgroundColorRevealed, ContextCompat.getColor(context, R.color.purple_200))
        _paintCellBackgroundRevealed[1].color = getColor(R.styleable.MinesweeperGame_cellBackgroundColorRevealed2, ContextCompat.getColor(context, R.color.purple_200))
        _paintCellBackgroundMine.color = getColor(R.styleable.MinesweeperGame_cellBackgroundColorMine, ContextCompat.getColor(context, R.color.purple_700))
        _paintCellValue.color = getColor(R.styleable.MinesweeperGame_cellValueColor, ContextCompat.getColor(context, R.color.white))
      } finally {
        recycle()
      }
    }
    _paintCellValue.textAlign = Paint.Align.CENTER
    _paintCellValue.typeface = Typeface.DEFAULT_BOLD

    _minesweeperEngine = MinesweeperGameEngine(_boardSize, _nbMines)
  }

  fun setMode(mode: ModeEnum) { _mode = mode }
  fun getMode(): ModeEnum = _mode

  private fun startTimer() {
    if (_isTimerRunning)
      return
    _timer = fixedRateTimer("minesweeper-timer", true, 1000, 1000) {
      println("Hello")
    }
    _isTimerRunning = true
  }

  private fun stopTimer() {
    if (!_isTimerRunning)
      return
    _timer.cancel()
    _isTimerRunning = false
  }

  fun reset() {
    stopTimer()
    _minesweeperEngine.reset()
    updateRemainingFlagsText()
    invalidate()
  }

  private fun drawCell(x: Int, y: Int, canvas: Canvas) {
    val cell = _minesweeperEngine.getCellAtPos(x, y)!!
    val topLeft = PointF(x * _cellSize, y * _cellSize)
    val bottomRight = PointF(x * _cellSize + _cellSize, y * _cellSize + _cellSize)
    val center = PointF(topLeft.x + _cellSize * 0.5f, topLeft.y + _cellSize * 0.7f)

    val rect = RectF(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y)

    if (!cell.isRevealed()) {
      canvas.drawRect(rect, _paintCellBackground[(x + y) % 2])
      if (_minesweeperEngine.getGameState() == MinesweeperGameEngine.StatesEnum.WIN)
        canvas.drawText(context.getString(R.string.mine), center.x, center.y, _paintCellValue)
      else if (cell.isFlagged())
        canvas.drawText(context.getString(R.string.flagging_mode), center.x, center.y, _paintCellValue)
    } else {
      if (cell.isMine()) {
        canvas.drawRect(rect, _paintCellBackgroundMine)
        canvas.drawText(context.getString(R.string.mine), center.x, center.y, _paintCellValue)
      } else {
        canvas.drawRect(rect, _paintCellBackgroundRevealed[(x + y) % 2])
        val cellValue = cell.getValue()
        if (cellValue != 0)
          canvas.drawText(cellValue.toString(), center.x, center.y, _paintCellValue)
      }
    }
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
    when(_minesweeperEngine.getGameState()) {
      MinesweeperGameEngine.StatesEnum.PLAYING -> Unit
      else -> return
    }
    val pos = Point(
      floor(touchX / _cellSize).toInt(),
      floor(touchY / _cellSize).toInt()
    )
    if (_mode == ModeEnum.UNCOVERING) {
      _minesweeperEngine.revealCellAtPos(pos.x, pos.y)
      startTimer()
      when(_minesweeperEngine.getGameState()) {
        MinesweeperGameEngine.StatesEnum.LOST -> {
          stopTimer()
          Toast.makeText(context, "You lost!", Toast.LENGTH_LONG).show()
        }
        MinesweeperGameEngine.StatesEnum.WIN -> {
          stopTimer()
          Toast.makeText(context, "You win!", Toast.LENGTH_LONG).show()
        }
        else -> {}
      }
    } else {
      _minesweeperEngine.flagCellAtPos(pos.x, pos.y)
    }
    updateRemainingFlagsText()
    invalidate()
  }

  fun updateRemainingFlagsText() {
    remainingFlagsTextView.text = context.getString(R.string.nb_remaining_flags).format(_minesweeperEngine.getRemainingFlagsCount())
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    manageBoardClick(event.x, event.y)
    return super.onTouchEvent(event)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val w = MeasureSpec.getSize(widthMeasureSpec)
    val h = MeasureSpec.getSize(heightMeasureSpec)
    val displaySize = min(w, h)
    val displaySizeMeasureSpec: Int = MeasureSpec.makeMeasureSpec(displaySize, MeasureSpec.EXACTLY)
    super.onMeasure(displaySizeMeasureSpec, displaySizeMeasureSpec)
    _cellSize = displaySize / _boardSize.toFloat()
    _paintCellValue.textSize = _cellSize * 0.6f
    invalidate()
    requestLayout()
  }
}
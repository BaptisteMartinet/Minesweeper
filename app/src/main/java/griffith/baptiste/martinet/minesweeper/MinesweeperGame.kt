package griffith.baptiste.martinet.minesweeper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit
import kotlin.math.floor
import kotlin.math.min

class MinesweeperGame(context: Context, attrs: AttributeSet) : View(context, attrs) {
  enum class ModeEnum {
    UNCOVERING,
    FLAGGING,
  }

  lateinit var remainingFlagsTextView: TextView
  lateinit var chronometer: Chronometer
  lateinit var bestTimeTextView: TextView

  private var _cellSize: Float = 0f
  private var _mode = ModeEnum.UNCOVERING
  private var _isTimerRunning: Boolean = false

  private val _paintCellBackground: List<Paint> = List(2) { Paint() }
  private val _paintCellBackgroundRevealed: List<Paint> = List(2) { Paint() }
  private val _paintCellBackgroundMine: Paint = Paint()
  private val _paintCellValue: Paint = TextPaint()

  private val _minesweeperEngine: MinesweeperGameEngine
  private val _db: DatabaseHelper

  private var _currentBestTime: Long = 0

  private var _displaySize: Int = 0

  private val _cellValueColors: IntArray

  init {
    var boardSize: Int
    var nbMines: Int
    context.theme.obtainStyledAttributes(attrs, R.styleable.MinesweeperGame, 0, 0).apply {
      try {
        boardSize = getInteger(R.styleable.MinesweeperGame_boardSize, 10)
        nbMines = getInteger(R.styleable.MinesweeperGame_nbMines, 20)

        _paintCellBackground[0].color = getColor(R.styleable.MinesweeperGame_cellBackgroundColor, ContextCompat.getColor(context, R.color.lightGreen))
        _paintCellBackground[1].color = getColor(R.styleable.MinesweeperGame_cellBackgroundColor2, ContextCompat.getColor(context, R.color.darkGreen))
        _paintCellBackgroundRevealed[0].color = getColor(R.styleable.MinesweeperGame_cellBackgroundColorRevealed, ContextCompat.getColor(context, R.color.lightBrown))
        _paintCellBackgroundRevealed[1].color = getColor(R.styleable.MinesweeperGame_cellBackgroundColorRevealed2, ContextCompat.getColor(context, R.color.darkBrown))
        _paintCellBackgroundMine.color = getColor(R.styleable.MinesweeperGame_cellBackgroundColorMine, ContextCompat.getColor(context, R.color.red))
        _paintCellValue.color = getColor(R.styleable.MinesweeperGame_cellValueColor, ContextCompat.getColor(context, R.color.white))
      } finally {
        recycle()
      }
    }
    _paintCellValue.textAlign = Paint.Align.CENTER
    _paintCellValue.typeface = Typeface.DEFAULT_BOLD

    _minesweeperEngine = MinesweeperGameEngine(boardSize, nbMines)
    _db = DatabaseHelper.getInstance(context)

    _cellValueColors = context.resources.getIntArray(R.array.valueColors)
  }

  fun setMode(mode: ModeEnum) { _mode = mode }

  fun getMode(): ModeEnum = _mode

  fun updateBoardSettings(boardSize: Int, nbMines: Int) {
    stopTimer()
    resetTimer()
    _minesweeperEngine.updateBoardSettings(boardSize, nbMines)
    updateDisplaySize()
    updateRemainingFlagsText()
    updateCurrentBestTime()
  }

  private fun startTimer() {
    if (!this::chronometer.isInitialized)
      return
    if (_isTimerRunning)
      return
    chronometer.base = SystemClock.elapsedRealtime()
    chronometer.start()
    _isTimerRunning = true
  }

  private fun stopTimer() {
    if (!this::chronometer.isInitialized)
      return
    if (!_isTimerRunning)
      return
    chronometer.stop()
    _isTimerRunning = false
  }

  private fun resetTimer() {
    if (!this::chronometer.isInitialized)
      return
    chronometer.base = SystemClock.elapsedRealtime()
  }

  private fun registerTimeToDatabase() {
    if (!this::chronometer.isInitialized)
      return
    val elapsedMillis: Long = SystemClock.elapsedRealtime() - chronometer.base
    val elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis)
    val bestTime = BestTime(elapsedSeconds, _minesweeperEngine.getBoardSize(), _minesweeperEngine.getNbMines())
    _db.insertBestTime(bestTime)
  }

  fun updateCurrentBestTime() {
    _currentBestTime = _db.getBestTime(_minesweeperEngine.getBoardSize(), _minesweeperEngine.getNbMines()) ?: 0
    if (!this::bestTimeTextView.isInitialized)
      return
    bestTimeTextView.text = _currentBestTime.toString()
  }

  fun reset() {
    stopTimer()
    resetTimer()
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
    _paintCellValue.color = context.getColor(R.color.white)

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
        if (cellValue != 0) {
          _paintCellValue.color = _cellValueColors[cellValue - 1]
          canvas.drawText(cellValue.toString(), center.x, center.y, _paintCellValue)
        }
      }
    }
  }

  private fun drawBoard(canvas: Canvas) {
    val boardSize: Int = _minesweeperEngine.getBoardSize()
    for (y in 0 until boardSize) {
      for (x in 0 until boardSize) {
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
          registerTimeToDatabase()
          updateCurrentBestTime()
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
    if (!this::remainingFlagsTextView.isInitialized)
      return
    remainingFlagsTextView.text = context.getString(R.string.nb_remaining_flags).format(_minesweeperEngine.getRemainingFlagsCount())
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    manageBoardClick(event.x, event.y)
    return super.onTouchEvent(event)
  }

  private fun updateDisplaySize() {
    _cellSize = _displaySize / _minesweeperEngine.getBoardSize().toFloat()
    _paintCellValue.textSize = _cellSize * 0.6f
    invalidate()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val w = MeasureSpec.getSize(widthMeasureSpec)
    val h = MeasureSpec.getSize(heightMeasureSpec)
    _displaySize = min(w, h)
    val displaySizeMeasureSpec: Int = MeasureSpec.makeMeasureSpec(_displaySize, MeasureSpec.EXACTLY)
    super.onMeasure(displaySizeMeasureSpec, displaySizeMeasureSpec)
    updateDisplaySize()
    requestLayout()
  }
}
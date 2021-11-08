package griffith.baptiste.martinet.minesweeper

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
  private lateinit var _minesweeperGame: MinesweeperGame

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val difficultySpinner = findViewById<Spinner>(R.id.difficultySpinner)
    ArrayAdapter.createFromResource(
      this,
      R.array.difficulties,
      R.layout.custom_spinner_item
    ).also { adapter ->
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
      difficultySpinner.adapter = adapter
      difficultySpinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
          val boardSize: Int
          val nbMines: Int
          when(pos) {
            0 -> { //simple
              boardSize = 9
              nbMines = 10
            }
            1 -> { //hard
              boardSize = 10
              nbMines = 20
            }
            2 -> { //expert
              boardSize = 16
              nbMines = 40
            }
            3 -> { //impossible
              boardSize = 30
              nbMines = 150
            }
            else -> {
              boardSize = 10
              nbMines = 20
            }
          }
          _minesweeperGame.updateBoardSettings(boardSize, nbMines)
          Toast.makeText(applicationContext, "Changed difficulty to ${parent?.getItemAtPosition(pos)}.\n(boardSize: $boardSize, nbMines: $nbMines)", Toast.LENGTH_SHORT).show()
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
      }
    }

    val remainingFlags = findViewById<TextView>(R.id.remainingFlags)
    val chronometer = findViewById<Chronometer>(R.id.chronometer)
    chronometer.text = "0"
    chronometer.setOnChronometerTickListener { chrono ->
      val elapsedMilliseconds = SystemClock.elapsedRealtime() - chrono.base
      val elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMilliseconds)
      chrono.text = elapsedSeconds.toString()
    }
    val bestTimeTextView = findViewById<TextView>(R.id.bestTime)
    _minesweeperGame = findViewById(R.id.game)

    _minesweeperGame.chronometer = chronometer
    _minesweeperGame.bestTimeTextView = bestTimeTextView
    _minesweeperGame.remainingFlagsTextView = remainingFlags
    _minesweeperGame.updateRemainingFlagsText()
    _minesweeperGame.updateCurrentBestTime()

    val resetBtn = findViewById<Button>(R.id.resetBtn)
    resetBtn.setOnClickListener {
      showDialog()
    }

    val modeBtn = findViewById<Button>(R.id.modeBtn)
    modeBtn.setOnClickListener {
      if (_minesweeperGame.getMode() == MinesweeperGame.ModeEnum.UNCOVERING) {
        modeBtn.text = getString(R.string.flagging_mode)
        _minesweeperGame.setMode(MinesweeperGame.ModeEnum.FLAGGING)
      } else {
        modeBtn.text = getString(R.string.uncovering_mode)
        _minesweeperGame.setMode(MinesweeperGame.ModeEnum.UNCOVERING)
      }
    }
  }

  private fun showDialog() {
    lateinit var dialog: AlertDialog
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Reset board?")
    builder.setMessage("Do you want to reset the game?")
    val dialogClickListener = DialogInterface.OnClickListener { _, which ->
      when (which) {
        DialogInterface.BUTTON_POSITIVE -> _minesweeperGame.reset()
      }
    }
    builder.setPositiveButton("YES", dialogClickListener)
    builder.setNegativeButton("CANCEL", dialogClickListener)
    dialog = builder.create()
    dialog.show()
  }
}
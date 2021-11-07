package griffith.baptiste.martinet.minesweeper

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.os.SystemClock
import java.util.concurrent.TimeUnit



class MainActivity : AppCompatActivity() {
  private lateinit var _minesweeperGame: MinesweeperGame

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

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
package griffith.baptiste.martinet.minesweeper

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
  private lateinit var minesweeperGame: MinesweeperGame

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val remainingFlags = findViewById<TextView>(R.id.remainingFlags)
    minesweeperGame = findViewById(R.id.game)
    minesweeperGame.remainingFlagsTextView = remainingFlags
    minesweeperGame.updateRemainingFlagsText()
    val resetBtn = findViewById<Button>(R.id.resetBtn)
    resetBtn.setOnClickListener {
      showDialog()
    }

    val modeBtn = findViewById<Button>(R.id.modeBtn)
    modeBtn.setOnClickListener {
      if (minesweeperGame.getMode() == MinesweeperGame.ModeEnum.UNCOVERING) {
        modeBtn.text = getString(R.string.flagging_mode)
        minesweeperGame.setMode(MinesweeperGame.ModeEnum.FLAGGING)
      } else {
        modeBtn.text = getString(R.string.uncovering_mode)
        minesweeperGame.setMode(MinesweeperGame.ModeEnum.UNCOVERING)
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
        DialogInterface.BUTTON_POSITIVE -> minesweeperGame.reset()
      }
    }
    builder.setPositiveButton("YES", dialogClickListener)
    builder.setNegativeButton("CANCEL", dialogClickListener)
    dialog = builder.create()
    dialog.show()
  }
}
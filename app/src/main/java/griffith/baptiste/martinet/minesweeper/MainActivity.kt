package griffith.baptiste.martinet.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val minesweeperGame = findViewById<MinesweeperGame>(R.id.game)
    val resetBtn = findViewById<Button>(R.id.resetBtn)
    resetBtn.setOnClickListener {
      minesweeperGame.reset()
    }

    val modeBtn = findViewById<Button>(R.id.modeBtn)
    modeBtn.setOnClickListener {
      if (minesweeperGame.getMode() == MinesweeperGame.ModeEnum.EDITING) {
        modeBtn.text = getString(R.string.flaggingMode)
        minesweeperGame.setMode(MinesweeperGame.ModeEnum.FLAGGING)
      } else {
        modeBtn.text = getString(R.string.editingMode)
        minesweeperGame.setMode(MinesweeperGame.ModeEnum.EDITING)
      }
    }
  }
}
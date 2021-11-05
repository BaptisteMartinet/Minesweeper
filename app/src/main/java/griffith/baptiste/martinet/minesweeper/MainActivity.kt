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
  }
}
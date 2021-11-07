package griffith.baptiste.martinet.minesweeper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper
private constructor(context: Context) :
  SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
  companion object {
    private var sqlInstance : DatabaseHelper? = null
    private const val DATABASE_NAME = "minesweeper"
    private const val DATABASE_VERSION = 1

    @Synchronized
    fun getInstance(context: Context): DatabaseHelper {
      if (sqlInstance == null) {
        sqlInstance = DatabaseHelper(context.applicationContext)
      }
      return sqlInstance!!
    }
  }

  private val bestTimesTableName: String = "best_times"

  private val createBestTimesTableQuery =
    "CREATE TABLE IF NOT EXISTS $bestTimesTableName (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "seconds INTEGER," +
        "boardSize INTEGER," +
        "nbMines INTEGER" +
        ")"

  private val deleteBestTimesTableQuery = "DROP TABLE $bestTimesTableName"

  private fun createDB(db: SQLiteDatabase?) {
    Log.i("DATABASE", "Creating database...")
    db?.execSQL(createBestTimesTableQuery)
  }

  private fun upgradeDB(db: SQLiteDatabase?) {
    Log.i("DATABASE", "Upgrading database...")
    db?.execSQL(deleteBestTimesTableQuery)
    createDB(db)
  }

  override fun onCreate(db: SQLiteDatabase?) {
    createDB(db)
  }

  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    upgradeDB(db)
  }

  override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    upgradeDB(db)
  }

  fun insertBestTime(bestTime: BestTime): Boolean {
    val db = sqlInstance!!.writableDatabase
    val contentValues = ContentValues()
    contentValues.put("seconds", bestTime.seconds)
    contentValues.put("boardSize", bestTime.boardSize)
    contentValues.put("nbMines", bestTime.nbMines)
    val result = db.insert(this.bestTimesTableName, null, contentValues)
    return (result > 0)
  }

  fun getBestTime(boardSize: Int, nbMines: Int): Long? {
    val db = sqlInstance!!.readableDatabase
    val cursor: Cursor = db.rawQuery("SELECT MIN(seconds) as bestTime FROM $bestTimesTableName WHERE boardSize=$boardSize AND nbMines=$nbMines", null)
    if (!cursor.moveToFirst())
      return null
    val bestTime = cursor.getLong(cursor.getColumnIndexOrThrow("bestTime"))
    cursor.close()
    return bestTime
  }
}

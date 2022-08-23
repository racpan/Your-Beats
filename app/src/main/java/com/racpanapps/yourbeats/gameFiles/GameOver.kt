package com.racpanapps.yourbeats.gameFiles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.InitializeBannerAds
import com.racpanapps.yourbeats.songsDB.HighScoreDB
import com.racpanapps.yourbeats.songsDB.SongRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GameOver : AppCompatActivity(), CoroutineScope {

    private var job : Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var constraintLayoutGameOver : ConstraintLayout
    private lateinit var linearLayoutBannerGameOver : LinearLayout
    private lateinit var textViewGameOverScore : TextView
    private lateinit var buttonGameOverQuit : Button
    private var level = ""
    private var listId : Long = 0
    private var points = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)
        constraintLayoutGameOver = findViewById(R.id.constraintLayoutGameOver)
        linearLayoutBannerGameOver = findViewById(R.id.linearLayoutBannerGameOver)
        textViewGameOverScore = findViewById(R.id.textViewGameOverScore)
        buttonGameOverQuit = findViewById(R.id.buttonGameOverQuit)
        InitializeBannerAds.setBackground(applicationContext, constraintLayoutGameOver)
        InitializeBannerAds.loadAds(application, this@GameOver, linearLayoutBannerGameOver)
        level = intent.getStringExtra("level")!!
        listId = intent.getLongExtra("listId", 0)
        points = intent.getIntExtra("points", 0)
        textViewGameOverScore.text = points.toString()
        buttonGameOverQuit.setOnClickListener { startActivity(Intent(applicationContext, SongList::class.java)) }
        setHighScore()
    }

    @WorkerThread
    private fun setHighScore() {
        launch {
            val db = SongRoomDatabase.getDatabase(applicationContext)
            try {
                var highScore = db.songsDao().getHighScore()
                if (highScore < points.toLong()) {
                    db.songsDao().insertHighScore(HighScoreDB(resources.getString(R.string.high_score_db), points.toLong()))
                }
            } catch (e : Exception) {
                val highScore = HighScoreDB(resources.getString(R.string.high_score_db), points.toLong())
                db.songsDao().insertHighScore(highScore)
            }
        }
     }
}
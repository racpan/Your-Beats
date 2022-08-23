package com.racpanapps.yourbeats.mainMenu

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.InitializeBannerAds
import com.racpanapps.yourbeats.songsDB.SongRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HighScore : AppCompatActivity(), CoroutineScope {

    private lateinit var linearLayoutBannerHighScore : LinearLayout
    private lateinit var textViewHighScore : TextView
    private lateinit var constraintLayoutHighScore : ConstraintLayout
    private lateinit var db : SongRoomDatabase

    private var job : Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.high_score)

        initializeControls()
        InitializeBannerAds.loadAds(application, this, linearLayoutBannerHighScore)
        InitializeBannerAds.setBackground(applicationContext, constraintLayoutHighScore)
        db = SongRoomDatabase.getDatabase(this)
        getHighScore()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainMenu::class.java))
    }

    private fun initializeControls() {
        linearLayoutBannerHighScore = findViewById(R.id.linearLayoutBannerHighScore)
        textViewHighScore = findViewById(R.id.textViewHighScore)
        constraintLayoutHighScore = findViewById(R.id.constraintLayoutHighScore)
    }

    @WorkerThread
    private fun getHighScore() {
        launch {
            val highScore : Long = try {
                db.songsDao().getHighScore()
            } catch (e : Exception) {
                0
            }
            if (highScore == 0L) {
                textViewHighScore.text = "0"
            } else {
                textViewHighScore.text = highScore.toString()
            }
        }
    }
}
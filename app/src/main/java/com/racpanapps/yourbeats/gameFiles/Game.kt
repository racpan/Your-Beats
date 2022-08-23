package com.racpanapps.yourbeats.gameFiles

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.InitializeBannerAds
import com.racpanapps.yourbeats.classes.MediaPlayerInstance


class Game : AppCompatActivity() {

    companion object {
        var level : String = ""
        var listId : Long = 0
        var songListRunning: Boolean = true
    }

    private lateinit var constraintLayoutGame : ConstraintLayout
    private lateinit var gameLayout : RelativeLayout
    private lateinit var textViewCountdown : TextView
    private lateinit var textViewPauseScore : TextView
    private lateinit var buttonGameQuit : Button
    private lateinit var appContext : Context
    private lateinit var linearLayoutPauseMenu : LinearLayout
    private lateinit var buttonResumeRestart : Button
    private lateinit var textViewScore : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game)
        appContext = applicationContext
        initializeControls()
        initializeListeners()
        InitializeBannerAds.setBackground(applicationContext, constraintLayoutGame)
        level = intent.getStringExtra("level")!!
        listId = intent.getIntExtra("listId", 0).toLong()
        countDown()
        linearLayoutPauseMenu.isEnabled = false
        linearLayoutPauseMenu.visibility = View.INVISIBLE
    }

    override fun onBackPressed() {
        if (!MediaPlayerInstance.pause) {
            pauseGame()
        } else {
            resumeGame()
        }
    }

    private fun initializeControls() {
        constraintLayoutGame = findViewById(R.id.constraintLayoutGame)
        gameLayout = findViewById(R.id.frameLayoutGame)
        textViewCountdown = findViewById(R.id.textViewCountdown)
        textViewScore = findViewById(R.id.textViewScore)
        linearLayoutPauseMenu = findViewById(R.id.linearLayoutPauseMenu)
        textViewPauseScore = findViewById(R.id.textViewPauseScore)
        buttonResumeRestart = findViewById(R.id.buttonResumeRestart)
        buttonGameQuit = findViewById(R.id.buttonGameQuit)
    }

    private fun initializeListeners() {
        buttonResumeRestart.setOnClickListener {
            if (MediaPlayerInstance.pause) {
                linearLayoutPauseMenu.isEnabled = false
                linearLayoutPauseMenu.visibility = View.INVISIBLE
                countDown()
            } else {
                val intent = Intent(applicationContext, GameLevel::class.java)
                intent.putExtra("listId", listId)
                startActivity(intent)
            }
        }
        buttonGameQuit.setOnClickListener {
            MediaPlayerInstance.stopSong()
            startActivity(Intent(applicationContext, SongList::class.java))
        }
    }

    private fun setScoreText() {
        textViewPauseScore.text = textViewScore.text
    }

    private fun pauseGame() {
        buttonResumeRestart.text = resources.getString(R.string.resume)
        textViewScore.visibility = View.INVISIBLE
        setScoreText()
        MediaPlayerInstance.pause()
        linearLayoutPauseMenu.isEnabled = true
        linearLayoutPauseMenu.visibility = View.VISIBLE
    }

    private fun resumeGame() {
        linearLayoutPauseMenu.isEnabled = false
        linearLayoutPauseMenu.visibility = View.INVISIBLE
        countDown()
    }

    private fun countDown() {
        val mp = MediaPlayer.create(applicationContext, R.raw.countdown)
        textViewCountdown.isEnabled = true
        textViewCountdown.visibility = View.VISIBLE
        object : CountDownTimer(2000, 500) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished in 1600..2000) {
                    textViewCountdown.text = getString(R.string.three)
                }
                if (millisUntilFinished in 1100..1599) {
                    textViewCountdown.text = getString(R.string.two)
                }
                if (millisUntilFinished in 600..1099) {
                    textViewCountdown.text = getString(R.string.one)
                }
                if (millisUntilFinished in 100..599) {
                    textViewCountdown.text = getString(R.string.go)
                }
            }
            override fun onFinish() {
                textViewCountdown.isEnabled = false
                textViewCountdown.visibility = View.INVISIBLE
                textViewScore.visibility = View.VISIBLE
                if (!MediaPlayerInstance.pause) {
                    MediaPlayerInstance.startGame(listId, gameLayout, textViewScore, this@Game, level)
                } else {
                    MediaPlayerInstance.play()
                }
            }
        }.start()
        mp.start()
    }
}
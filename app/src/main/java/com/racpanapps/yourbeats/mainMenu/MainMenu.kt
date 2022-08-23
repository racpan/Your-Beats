package com.racpanapps.yourbeats.mainMenu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.InitializeBannerAds
import com.racpanapps.yourbeats.classes.MediaPlayerInstance
import com.racpanapps.yourbeats.gameFiles.SongList

class MainMenu : AppCompatActivity() {

    private lateinit var buttonStartGame : Button
    private lateinit var buttonMusicPlayer : Button
    private lateinit var buttonHowToPlay : Button
    private lateinit var buttonHighScore : Button
    private lateinit var buttonContactUs : Button
    private lateinit var buttonSettings : Button
    private lateinit var buttonQuit : Button
    private lateinit var linearLayoutBanner : LinearLayout
    private lateinit var constraintLayout : ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        declareVariables()
        declareListeners()
        InitializeBannerAds.loadAds(application, this, linearLayoutBanner)
        askPermission()
        InitializeBannerAds.setBackground(this, constraintLayout)
    }

    override fun onBackPressed() {
        quit()
    }

    private fun declareVariables() {
        buttonStartGame = findViewById(R.id.buttonStartGame)
        buttonMusicPlayer = findViewById(R.id.buttonMusicPlayer)
        buttonHowToPlay = findViewById(R.id.buttonHowtoPlay)
        buttonHighScore = findViewById(R.id.buttonHighScore)
        buttonContactUs = findViewById(R.id.buttonContactUs)
        buttonSettings = findViewById(R.id.buttonSettings)
        buttonQuit = findViewById(R.id.buttonQuit)
        linearLayoutBanner = findViewById(R.id.linearLayoutBannerHighScore)
        constraintLayout = findViewById(R.id.constraintLayoutMainMenu)
    }

    private fun declareListeners() {
        buttonStartGame.setOnClickListener {
            startActivity(Intent(this, SongList::class.java))
        }
        buttonMusicPlayer.setOnClickListener {
            startActivity(Intent(this, MusicPlayer::class.java))
        }
        buttonHowToPlay.setOnClickListener {
            startActivity(Intent(this, HowToPlay::class.java))
        }
        buttonHighScore.setOnClickListener {
            startActivity(Intent(this, HighScore::class.java))
        }
        buttonContactUs.setOnClickListener {
            startActivity(Intent(this, ContactUs::class.java))
        }
        buttonSettings.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }
        buttonQuit.setOnClickListener {
            quit()
        }
    }

    private fun quit() {
        MediaPlayerInstance.stopSong()
        finishAffinity()
    }

    private fun askPermission() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissionGranted = PackageManager.PERMISSION_GRANTED
        if (permission != permissionGranted) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        }
    }
}

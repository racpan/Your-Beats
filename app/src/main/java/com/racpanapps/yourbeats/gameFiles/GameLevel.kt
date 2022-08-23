package com.racpanapps.yourbeats.gameFiles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.InitializeBannerAds

class GameLevel : AppCompatActivity() {

    private lateinit var buttonNormal : Button
    private lateinit var buttonHard : Button
    private lateinit var constraintLayoutGameLevel : ConstraintLayout
    private lateinit var linearLayoutBannerGameLevel : LinearLayout
    private var listId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_level)
        listId = intent.getIntExtra("listId", -1)
        initializeControls()
        initializeListeners()
        InitializeBannerAds.loadAds(application, this, linearLayoutBannerGameLevel)
        InitializeBannerAds.setBackground(applicationContext, constraintLayoutGameLevel)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SongList::class.java))
    }

    private fun initializeControls() {
        buttonNormal = findViewById(R.id.buttonNormal)
        buttonHard = findViewById(R.id.buttonHard)
        constraintLayoutGameLevel = findViewById(R.id.constraintLayoutGameLevel)
        linearLayoutBannerGameLevel = findViewById(R.id.linearLayoutBannerGameLevel)
    }

    private fun initializeListeners() {
        val newIntent = Intent(applicationContext, Game::class.java)
        newIntent.putExtra("listId", listId)
        buttonHard.setOnClickListener {
            if (listId == -1) {
                songNotFoundToast()
            } else {
                newIntent.putExtra("level", getString(R.string.hard))
                startActivity(newIntent)
            }
        }
        buttonNormal.setOnClickListener {
            if (listId == -1) {
                songNotFoundToast()
            } else {
                newIntent.putExtra("level", getString(R.string.normal))
                startActivity(newIntent)
            }
        }
    }

    private fun songNotFoundToast() {
        Toast.makeText(applicationContext, R.string.song_not_found, Toast.LENGTH_LONG).show()
    }
}
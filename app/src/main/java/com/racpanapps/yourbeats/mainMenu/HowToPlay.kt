package com.racpanapps.yourbeats.mainMenu

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.InitializeBannerAds

class HowToPlay : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.how_to_play)

        val constraintLayoutHowToPlay = findViewById<ConstraintLayout>(R.id.constraintLayoutHowToPlay)
        val linearLayoutBannerHowToPlay = findViewById<LinearLayout>(R.id.linearLayoutBannerHowToPlay)
        InitializeBannerAds.loadAds(application, this, linearLayoutBannerHowToPlay)
        InitializeBannerAds.setBackground(application, constraintLayoutHowToPlay)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainMenu::class.java))
    }
}
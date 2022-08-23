package com.racpanapps.yourbeats.mainMenu

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.racpanapps.yourbeats.R

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 32)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainMenu::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        startActivity(Intent(this, MainMenu::class.java))
    }
}
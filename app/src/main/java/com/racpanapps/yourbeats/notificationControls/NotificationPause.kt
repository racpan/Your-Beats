package com.racpanapps.yourbeats.notificationControls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.MediaPlayerInstance

class NotificationPause : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.getStringExtra("action")
        if (action.toString() == context?.resources?.getString(R.string.pause)) {
            if (MediaPlayerInstance.pause) {
                MediaPlayerInstance.play()
            } else {
                MediaPlayerInstance.pause()
            }
        }
    }
}
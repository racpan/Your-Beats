package com.racpanapps.yourbeats.notificationControls

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.mainMenu.MusicPlayer

class Notifications {

    fun buildNotification(act : Activity, appContext : Context, songName : String) {

        val channelId = appContext.resources.getString(R.string.app_id)
        val title = appContext.resources.getString(R.string.app_name)

        val result = Intent(act, MusicPlayer::class.java)
        val previousIntent = Intent(appContext, NotificationPrevious::class.java).putExtra("action", "Previous")
        val pauseIntent = Intent(appContext, NotificationPause::class.java).putExtra("action", "Pause")
        val nextIntent = Intent(appContext, NotificationNext::class.java).putExtra("action", "Next")

        val pendingIntent = PendingIntent.getActivity(appContext, 0, result, 0)
        val previousPendingIntent = PendingIntent.getBroadcast(appContext, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val pausePendingIntent = PendingIntent.getBroadcast(appContext, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val nextPendingIntent = PendingIntent.getBroadcast(appContext, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val previousAction : Notification.Action = Notification.Action.Builder(Icon.createWithResource(appContext, R.drawable.ic_previous), appContext.resources.getString(R.string.previous), previousPendingIntent).build()
        val pauseAction : Notification.Action = Notification.Action.Builder(Icon.createWithResource(appContext, R.drawable.ic_pause), appContext.resources.getString(R.string.pause), pausePendingIntent).build()
        val nextAction : Notification.Action = Notification.Action.Builder(Icon.createWithResource(appContext, R.drawable.ic_next), appContext.resources.getString(R.string.next), nextPendingIntent).build()

        val notificationManager : NotificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, title,  NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
        var builder = Notification.Builder(appContext, System.currentTimeMillis().toString())
                .addAction(previousAction)
                .addAction(pauseAction)
                .addAction(nextAction)
                .setChannelId(channelId)
                .setContentIntent(pendingIntent)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(songName)
        notificationManager.notify(32, builder.build())
    }
}
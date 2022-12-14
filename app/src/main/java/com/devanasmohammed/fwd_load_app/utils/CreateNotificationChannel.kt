package com.devanasmohammed.fwd_load_app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun createNotificationChannel(
    context: Context,
    channelId: String,
    channelNameOnSettings: String,
    channelDescriptionOnSettings: String,
    importance: Int,

    ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId, channelNameOnSettings,
            importance
        )
        channel.description = channelDescriptionOnSettings
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
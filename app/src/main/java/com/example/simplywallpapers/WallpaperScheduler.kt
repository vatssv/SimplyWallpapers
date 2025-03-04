package com.example.wallpaperrotator

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.concurrent.TimeUnit

object WallpaperScheduler {

    fun scheduleWallpaperChanges(context: Context, folderPath: String?, imagePaths: List<String>, intervalInHours: Int) {
        val intent = Intent(context, WallpaperChangeReceiver::class.java)
        intent.putExtra("folderPath", folderPath)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(intervalInHours.toLong())
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}
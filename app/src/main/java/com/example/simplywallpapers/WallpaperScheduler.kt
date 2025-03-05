package com.example.wallpaperrotator

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import java.util.Calendar

object WallpaperScheduler {

    fun scheduleWallpaperChanges(context: Context, folderUriString: String?, imagePaths: List<String>, intervalInHours: Int) {
        Log.d("WallpaperScheduler", "Scheduling wallpaper changes with interval: $intervalInHours hours")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WallpaperChangeReceiver::class.java).apply {
            putExtra("FOLDER_URI", folderUriString)
            putStringArrayListExtra("IMAGE_PATHS", ArrayList(imagePaths))
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val intervalInMillis = intervalInHours * 60 * 60 * 1000L // Convert hours to milliseconds

        // Set the repeating alarm
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + intervalInMillis,
            intervalInMillis,
            pendingIntent
        )

        val wallpaperSetter = WallpaperSetter(context)
        val currentWallpaper = imagePaths.random()
        wallpaperSetter.setWallpaper(currentWallpaper)
    }
}
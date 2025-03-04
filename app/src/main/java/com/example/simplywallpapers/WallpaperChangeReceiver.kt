package com.example.wallpaperrotator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.wallpaperrotator.data.ImageDataSource
import com.example.wallpaperrotator.wallpaper.WallpaperSetter

class WallpaperChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val folderPath = intent.getStringExtra("folderPath")
        if (folderPath != null) {
            val imageDataSource = ImageDataSource(context)
            val wallpaperSetter = WallpaperSetter()
            val imagePaths = imageDataSource.getImagesFromFolder(folderPath)
            if (imagePaths.isNotEmpty()) {
                val randomIndex = (0 until imagePaths.size).random()
                val imagePath = imagePaths[randomIndex]
                WallpaperSetter.setWallpaper(context, imagePath)
            }
        }
    }
}
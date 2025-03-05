package com.example.wallpaperrotator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.wallpaperrotator.data.ImageDataSource

class WallpaperChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WallpaperChangeReceiver", "Wallpaper change alarm triggered")
        val folderUriString = intent.getStringExtra("FOLDER_URI")
        val imagePaths = intent.getStringArrayListExtra("IMAGE_PATHS")
        if (folderUriString != null && imagePaths != null && imagePaths.isNotEmpty()) {
            val imageDataSource = ImageDataSource(context)
            val wallpaperSetter = WallpaperSetter(context)
            val currentWallpaper = imagePaths.random()
            wallpaperSetter.setWallpaper(currentWallpaper)
        } else {
            Log.e("WallpaperChangeReceiver", "Missing folder URI or image paths")
        }
    }
}
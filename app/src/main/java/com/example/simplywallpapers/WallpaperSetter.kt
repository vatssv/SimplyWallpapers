package com.example.wallpaperrotator.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.app.WallpaperManager

class WallpaperSetter {

    companion object {
        fun setWallpaper(context: Context, imagePath: String) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val wallpaperManager = WallpaperManager.getInstance(context)
            try {
                wallpaperManager.setBitmap(bitmap)
            } catch (e: Exception) {
                // Handle potential exceptions
            }
        }
    }
}
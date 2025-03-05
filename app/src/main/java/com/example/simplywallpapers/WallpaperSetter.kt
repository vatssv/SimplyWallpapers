package com.example.wallpaperrotator

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.IOException

class WallpaperSetter(private val context: Context) {

    fun setWallpaper(imagePath: String) {
        Log.d("WallpaperSetter", "Setting wallpaper: $imagePath")
        val wallpaperManager = WallpaperManager.getInstance(context)
        try {
            val uri = Uri.parse(imagePath)
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val bitmap = BitmapFactory.decodeStream(inputStream)
                wallpaperManager.setBitmap(bitmap)
                inputStream.close()
            } else {
                Log.e("WallpaperSetter", "Could not open input stream for URI: $uri")
            }
        } catch (e: IOException) {
            Log.e("WallpaperSetter", "Error setting wallpaper", e)
        }
    }
}
package com.example.simplywallpapers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.wallpaperrotator.WallpaperScheduler
import com.example.wallpaperrotator.data.ImageDataSource
import com.example.wallpaperrotator.ui.FolderPicker
import com.example.wallpaperrotator.ui.IntervalSelector

class MainActivity : Activity() {

    private lateinit var folderPicker: FolderPicker
    private lateinit var intervalSelector: IntervalSelector
    private lateinit var pickFolderButton: Button

    private var folderPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        pickFolderButton = findViewById(R.id.pickFolderButton)

        loadFolderPath()

        intervalSelector = IntervalSelector(this, object: IntervalSelector.IntervalSelectedListener {
            override fun onIntervalSelected(intervalInHours: Int) {
                setWallpaper(intervalInHours)
            }
        })
        folderPicker = FolderPicker(this, object: FolderPicker.FolderSelectedListener {
            override fun onFolderSelected(folderPath: String) {
                this@MainActivity.folderPath = folderPath
                saveFolderPath(folderPath)
                intervalSelector.showIntervalSelector()
            }
            override fun onFolderSelectionFailed(errorMessage: String) {
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
        pickFolderButton.setOnClickListener {
            folderPicker.showFolderPicker()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FolderPicker.REQUEST_CODE_PICK_FOLDER) {
            folderPicker.handleActivityResult(resultCode, data)
        }
    }

    private fun saveFolderPath(folderPath: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("folderPath", folderPath)
        editor.apply()
    }

    private fun loadFolderPath() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        folderPath = sharedPreferences.getString("folderPath", null)
        if (folderPath != null) {
            Toast.makeText(this, "Loaded folder path: $folderPath", Toast.LENGTH_LONG).show()
        }
    }

    private fun setWallpaper(intervalInHours: Int) {
        val imageDataSource = ImageDataSource(this)
        val imagePaths = imageDataSource.getImagesFromFolder(this.folderPath)
        Log.d("MainActivity", "Image paths: $imagePaths")
        Log.d("MainActivity", "Folder path: $folderPath")
        if (imagePaths.isNotEmpty()) {
            WallpaperScheduler.scheduleWallpaperChanges(this, this.folderPath, imagePaths, intervalInHours)
            Toast.makeText(this, "Wallpaper rotation scheduled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No images found in the selected folder.", Toast.LENGTH_SHORT).show()
        }
    }
}
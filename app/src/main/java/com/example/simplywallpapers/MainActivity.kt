package com.example.simplywallpapers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperrotator.WallpaperScheduler
import com.example.wallpaperrotator.WallpaperSetter
import com.example.wallpaperrotator.data.ImageDataSource
import com.example.wallpaperrotator.data.ImageItem
import com.example.wallpaperrotator.ui.FolderPicker
import com.example.wallpaperrotator.ui.ImageAdapter
import com.example.wallpaperrotator.ui.IntervalSelector
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), ImageAdapter.OnImageClickListener,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var folderPicker: FolderPicker
    private lateinit var intervalSelector: IntervalSelector
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var imageList: MutableList<ImageItem>
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    private var folderPath: String? = null
    private var folderUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)
        imageRecyclerView = findViewById(R.id.imageRecyclerView)

        imageList = mutableListOf()
        imageAdapter = ImageAdapter(this, imageList, this)
        imageRecyclerView.adapter = imageAdapter
        imageRecyclerView.layoutManager = GridLayoutManager(this, 3)

        loadFolderPath()

        intervalSelector = IntervalSelector(this, object : IntervalSelector.IntervalSelectedListener {
            override fun onIntervalSelected(intervalInHours: Int) {
                setWallpaper(intervalInHours)
            }
        })
        folderPicker = FolderPicker(this, object : FolderPicker.FolderSelectedListener {
            override fun onFolderSelected(folderPath: String) {
                this@MainActivity.folderPath = folderPath
                folderUri = folderPicker.getFolderUri()
                saveFolderPath(folderPath, folderUri)
                loadImages()
            }

            override fun onFolderSelectionFailed(errorMessage: String) {
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })

        setupDrawer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FolderPicker.REQUEST_CODE_PICK_FOLDER) {
            folderPicker.handleActivityResult(resultCode, data)
        }
    }

    private fun loadFolderPath() {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        folderPath = sharedPreferences.getString("folderPath", null)
        folderUri = sharedPreferences.getString("folderUri", null)
        if (folderPath != null && folderUri != null) {
            loadImages()
        }
    }

    private fun saveFolderPath(folderPath: String, folderUri: String?) {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("folderPath", folderPath)
            putString("folderUri", folderUri)
            apply()
        }
    }

    private fun loadImages() {
        if (folderPath != null && folderUri != null) {
            val imageDataSource = ImageDataSource(this)
            val imagePaths = imageDataSource.getImagesFromFolder(folderUri!!)
            imageList.clear()
            imagePaths.forEach { path ->
                imageList.add(ImageItem(path))
            }
            imageAdapter.notifyDataSetChanged()
        }
    }

    private fun setWallpaper(intervalInHours: Int) {
        val selectedImages = imageList.filter { it.isSelected }.map { it.imagePath }
        if (selectedImages.isNotEmpty()) {
            WallpaperScheduler.scheduleWallpaperChanges(this, folderUri, selectedImages, intervalInHours)
            val wallpaperSetter = WallpaperSetter(this)
            wallpaperSetter.setWallpaper(selectedImages.random())
        } else {
            Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onImageClick(imageItem: ImageItem) {
        imageItem.isSelected = !imageItem.isSelected
        imageAdapter.notifyDataSetChanged()
    }

    private fun setupDrawer() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.pickFolder -> {
                folderPicker.showFolderPicker()
                drawerLayout.closeDrawers()
                return true
            }

            R.id.setWallpaper -> {
                intervalSelector.showIntervalSelector()
                drawerLayout.closeDrawers()
                return true
            }

            else -> return false
        }
    }
}
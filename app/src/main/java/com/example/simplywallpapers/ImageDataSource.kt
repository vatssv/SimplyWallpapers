package com.example.wallpaperrotator.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.vector.path
import androidx.documentfile.provider.DocumentFile

class ImageDataSource(private val context: Context) {

    fun getImagesFromFolder(folderUriString: String?): List<String> {
        val imagePaths = mutableListOf<String>()
        if (folderUriString == null) {
            Log.d("ImageDataSource", "Folder URI is null")
            return imagePaths
        }

        val folderUri = Uri.parse(folderUriString)
        val documentFile = DocumentFile.fromTreeUri(context, folderUri)

        if (documentFile != null && documentFile.isDirectory) {
            val contentResolver = context.contentResolver
            val children = documentFile.listFiles()
            children.forEach { child ->
                if (child.isFile && isImageFile(child.name ?: "")) {
                    val childUri = child.uri
                    val imagePath = getImagePathFromUri(contentResolver, childUri)
                    if (imagePath != null) {
                        imagePaths.add(imagePath)
                    }
                }
            }
        }
//        val imagePathsString = imagePaths.joinToString(", ")
//        Toast.makeText(context, "Loaded folder path: $imagePathsString", Toast.LENGTH_LONG).show()
        return imagePaths
    }

    private fun isImageFile(fileName: String): Boolean {
        return fileName.endsWith(".jpg", ignoreCase = true) ||
                fileName.endsWith(".jpeg", ignoreCase = true) ||
                fileName.endsWith(".png", ignoreCase = true)
    }

    private fun getImagePathFromUri(contentResolver: ContentResolver, uri: Uri): String? {
        Log.d("ImageDataSource", "getImagePathFromUri called with URI: $uri")
        val documentFile = DocumentFile.fromSingleUri(context, uri)
        if (documentFile != null && documentFile.isFile) {
            Log.d("ImageDataSource", "File name: ${documentFile.name}")
            return documentFile.uri.toString()
        } else {
            Log.e("ImageDataSource", "Could not get file from URI: $uri")
            return null
        }
    }
}
package com.example.wallpaperrotator.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile

class FolderPicker(private val activity: Activity, private val listener: FolderSelectedListener) {

    companion object {
        public const val REQUEST_CODE_PICK_FOLDER = 100
    }

    fun showFolderPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_FOLDER)
    }

    fun handleActivityResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val selectedFolderUri = data?.data
            if (selectedFolderUri != null) {
                val selectedFolderPath = getFolderPathFromUri(selectedFolderUri)
                if (selectedFolderPath != null) {
                    listener.onFolderSelected(selectedFolderPath)
                } else {
                    // Handle the case where the folder path could not be retrieved
                    listener.onFolderSelectionFailed("Could not retrieve folder path")
                }
            } else {
                // Handle the case where no URI was returned
                listener.onFolderSelectionFailed("No folder selected")
            }
        } else {
            // Handle the case where the user canceled the folder selection
            listener.onFolderSelectionFailed("Folder selection cancelled")
        }
    }

    private fun getFolderPathFromUri(uri: Uri): String? {
        return try {
            val documentFile = DocumentFile.fromTreeUri(activity, uri)
            if (documentFile != null && documentFile.isDirectory) {
                // Use the URI directly, not a file path
                uri.toString()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FolderPicker", "Error getting folder path from URI", e)
            null
        }
    }

    interface FolderSelectedListener {
        fun onFolderSelected(folderPath: String)
        fun onFolderSelectionFailed(errorMessage: String)
    }
}
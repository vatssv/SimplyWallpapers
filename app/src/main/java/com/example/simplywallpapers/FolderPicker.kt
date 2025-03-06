package com.example.wallpaperrotator.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.vector.path
import androidx.core.app.ActivityCompat.startActivityForResult

class FolderPicker(private val activity: Activity, private val listener: FolderSelectedListener) {

    interface FolderSelectedListener {
        fun onFolderSelected(folderPath: String)
        fun onFolderSelectionFailed(errorMessage: String)
    }

    companion object {
        const val REQUEST_CODE_PICK_FOLDER = 1001
    }

    private var folderPath: String? = null
    private var folderUri: Uri? = null

    fun showFolderPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        }
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_FOLDER)
    }

    fun handleActivityResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                folderUri = uri
                val folderPath = getFolderPathFromUri(uri)
                if (folderPath != null) {
                    listener.onFolderSelected(folderPath)
                } else {
                    listener.onFolderSelectionFailed("Failed to get folder path from URI")
                }
            } ?: listener.onFolderSelectionFailed("No folder selected")
        } else {
            listener.onFolderSelectionFailed("Folder selection cancelled")
        }
    }

    private fun getFolderPathFromUri(uri: Uri): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val documentId = DocumentsContract.getTreeDocumentId(uri)
            val path = documentId.split(":").lastOrNull()
            path?.let {
                Environment.getExternalStorageDirectory().path + "/" + it
            }
        } else {
            uri.path
        }
    }

    fun getFolderUri(): String? {
        return folderUri?.toString()
    }
}
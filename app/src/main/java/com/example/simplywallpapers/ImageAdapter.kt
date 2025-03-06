package com.example.wallpaperrotator.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplywallpapers.R
import com.example.wallpaperrotator.data.ImageItem

class ImageAdapter(
    private val context: Context,
    private val imageList: MutableList<ImageItem>,
    private val onImageClickListener: OnImageClickListener
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    interface OnImageClickListener {
        fun onImageClick(imageItem: ImageItem)
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentItem = imageList[position]
        val uri = Uri.parse(currentItem.imagePath)
        Glide.with(context)
            .load(uri)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onImageClickListener.onImageClick(currentItem)
        }
    }

    override fun getItemCount(): Int = imageList.size
}
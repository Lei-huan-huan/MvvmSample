package com.lhh.mvvmsample.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.lhh.mvvmsample.R
import com.lhh.mvvmsample.data.local.ImageEntity
import com.lhh.mvvmsample.databinding.ItemImageBinding

class ImageListAdapter : ListAdapter<ImageEntity, ImageViewHolder>(ImageDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.textName.text = item.name
        holder.binding.textMeta.text = holder.itemView.context.getString(
            R.string.image_meta_format,
            item.size
        )
        holder.binding.textUrl.text = item.url

        Glide.with(holder.binding.imagePreview)
            .load(item.url)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.binding.imagePreview)
    }
}

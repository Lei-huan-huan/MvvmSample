package com.lhh.mvvmsample.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.lhh.mvvmsample.data.local.ImageEntity

object ImageDiffCallback : DiffUtil.ItemCallback<ImageEntity>() {
    override fun areItemsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean {
        return oldItem == newItem
    }
}

package com.example.love

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout

class PhotoAdapter(private val context: Context, private val photos: List<Int>) : BaseAdapter() {
    override fun getCount() = photos.size
    override fun getItem(position: Int) = photos[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = ImageView(context).apply {
            setImageResource(photos[position])
            layoutParams = LinearLayout.LayoutParams(100, 100)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return imageView
    }
}
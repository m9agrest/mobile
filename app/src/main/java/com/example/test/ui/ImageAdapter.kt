package com.example.test.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import com.example.test.pojo.Photo
import loadImageFromUrl

class ImageAdapter(private val context: Context, private val images: List<Photo>) : BaseAdapter() {

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView

        if (convertView == null) {
            imageView = ImageView(context)
            /*imageView = ImageView(context).apply {
                layoutParams = GridView.LayoutParams(200, 200) // Устанавливаем размер изображения
                scaleType = ImageView.ScaleType.CENTER_CROP // Масштабируем изображение
            }*/
        } else {
            imageView = convertView as ImageView
        }

        // Загружаем изображение с помощью Picasso
        /*Picasso.get()
            .load(images[position].url)  // Предполагаем, что у Photo есть метод getUrl()
            .into(imageView)*/
        loadImageFromUrl(imageView, images[position])
        return imageView
    }
}

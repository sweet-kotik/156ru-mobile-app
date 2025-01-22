package com.example.a156ru

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class AdvertAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<Advert, AdvertAdapter.ViewHolder>(DiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(advert: Advert)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_advert, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(advert: Advert, listener: OnItemClickListener) {
            with(itemView) {
                findViewById<TextView>(R.id.tvTitle).text = advert.title
                findViewById<TextView>(R.id.tvCity).text = advert.city
                findViewById<TextView>(R.id.tvDate).text = advert.pubDate

                val imageView = findViewById<ImageView>(R.id.ivImage)

                advert.img?.takeIf { it.isNotBlank() }?.let { url ->
                    Glide.with(context)
                        .`as`(Bitmap::class.java)
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error_image)
                        .into(imageView)
                } ?: imageView.setImageResource(R.drawable.error_image)

                setOnClickListener { listener.onItemClick(advert) }
            }
        }
    }



    class DiffCallback : DiffUtil.ItemCallback<Advert>() {
        override fun areItemsTheSame(oldItem: Advert, newItem: Advert) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Advert, newItem: Advert) = oldItem == newItem
    }
}
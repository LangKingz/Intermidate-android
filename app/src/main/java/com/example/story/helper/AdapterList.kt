package com.example.story.helper

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.story.data.Response.ListStoryItem
import com.example.story.databinding.CardstoryBinding
import com.example.story.databinding.LoadingBinding
import com.example.story.view.detail.DetailActivity

class AdapterList : PagingDataAdapter<ListStoryItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private val VIEW_TYPE_ITEM = 1
    private val VIEW_TYPE_LOADING = 2

    class ViewHolder(private val binding: CardstoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.nama.text = story.name
            Glide.with(binding.root)
                .load(story.photoUrl)
                .into(binding.photo)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ID, story.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    class LoadingViewHolder(binding: LoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        // Kosongkan jika tidak ada yang perlu di-bind untuk loading
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is ListStoryItem) VIEW_TYPE_ITEM else VIEW_TYPE_LOADING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val binding = CardstoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolder(binding)
            }
            VIEW_TYPE_LOADING -> {
                val binding = LoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val story = getItem(position) as ListStoryItem
                holder.bind(story)
            }
            is LoadingViewHolder -> {
                // Tidak perlu bind apa-apa untuk loading
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id // Bandingkan ID untuk item yang sama
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem // Bandingkan konten item
            }
        }
    }
}
package com.alok.groww.Search.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alok.groww.R
import com.alok.groww.Search.domain.models.SearchItem
import com.alok.groww.databinding.ItemSearchBinding

class SearchItemAdapter(val clickListener: OnItemClickListener) : ListAdapter<SearchItem, SearchItemAdapter.SearchItemViewHolder>(SearchItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SearchItemViewHolder(binding).apply {
            itemView.setOnClickListener{
            if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                clickListener.onItemClick(position = bindingAdapterPosition)
            }
        }
        }
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val searchItem = getItem(position)
        holder.bind(searchItem)
    }

    class SearchItemViewHolder(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchItem: SearchItem) {
            binding.title.text = searchItem.name
            binding.subtitle.text = searchItem.symbol
            binding.type.text = searchItem.type
        }
    }

    class SearchItemDiffCallback : DiffUtil.ItemCallback<SearchItem>() {
        override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}

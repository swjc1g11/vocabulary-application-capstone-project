package com.swjcook.vocabularyapplication.ui.vocabularylistsoverview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swjcook.vocabularyapplication.databinding.VocabularyWithStateSummaryCardBinding
import com.swjcook.vocabularyapplication.domain.VocabularyListWithState

interface VocabularyListOverviewAdapterListener {
    fun onVocabularyListClick(listWithState: VocabularyListWithState)
}

class VocabularyListOverviewAdapter(val listener: VocabularyListOverviewAdapterListener) :
        ListAdapter<VocabularyListWithState, VocabularyListOverviewAdapter.VocabularyListWithStateViewHolder>(VocabularyListOverviewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabularyListWithStateViewHolder {
        return VocabularyListWithStateViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: VocabularyListWithStateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VocabularyListWithStateViewHolder private constructor(
            val binding: VocabularyWithStateSummaryCardBinding,
            val listListener: VocabularyListOverviewAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VocabularyListWithState) {
            binding.vocabularyListWithState = item
            binding.listener = listListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, listListener: VocabularyListOverviewAdapterListener): VocabularyListWithStateViewHolder {
                val binding = VocabularyWithStateSummaryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return VocabularyListWithStateViewHolder(binding, listListener)
            }
        }
    }

    class VocabularyListOverviewDiffCallback : DiffUtil.ItemCallback<VocabularyListWithState>() {
        override fun areItemsTheSame(oldItem: VocabularyListWithState, newItem: VocabularyListWithState): Boolean {
            return oldItem.list._id == newItem.list._id
        }

        override fun areContentsTheSame(oldItem: VocabularyListWithState, newItem: VocabularyListWithState): Boolean {
            return oldItem == newItem
        }
    }
}
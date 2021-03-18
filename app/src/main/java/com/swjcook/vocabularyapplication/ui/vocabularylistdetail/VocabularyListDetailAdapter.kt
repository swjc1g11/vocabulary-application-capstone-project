package com.swjcook.vocabularyapplication.ui.vocabularylistdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swjcook.vocabularyapplication.R
import com.swjcook.vocabularyapplication.databinding.VocabularyDetailListHeaderBinding
import com.swjcook.vocabularyapplication.databinding.VocabularyDetailWordWithStateItemBinding
import com.swjcook.vocabularyapplication.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

const val LIST_HEADER_ITEM = 1000
const val LIST_WORD_ITEM = 1001

interface VocabularyListDetailAdapterListener {
    fun onPracticeClick()
    fun onAddEditReminderClick(currentListStudyReminder: VocabularyListStudyReminder?, canShowStudyReminderForDate: Date?)
}

class VocabularyListDetailAdapter(val items: MutableList<VocabularyListDetailItem>, val listener : VocabularyListDetailAdapterListener)
    : ListAdapter<VocabularyListDetailItem, RecyclerView.ViewHolder>(VocabularyListDetailAdapter.DiffCallBack()) {

    init {
        submitList(items)
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeader(list: VocabularyList) {
         if (items.isNotEmpty()) {
             val item = getItem(0)
             if (item !is VocabularyListDetailItem.VocabularyListItem) {
                 items.add(0, VocabularyListDetailItem.VocabularyListItem(list))
             } else {
                 items.set(0, VocabularyListDetailItem.VocabularyListItem(list))
             }
         } else {
            items.add(VocabularyListDetailItem.VocabularyListItem(list))
         }

        notifyDataSetChanged()
    }

    fun addWords(words: List<WordWithState>) {
        adapterScope.launch {
            val tempList = mutableListOf<VocabularyListDetailItem>()
            if (items.isNotEmpty()) {
                val header = items.get(0)
                if (header is VocabularyListDetailItem.VocabularyListItem) {
                    tempList.add(header)
                }
            }
            words.forEach({
                tempList.add(VocabularyListDetailItem.WordWithStateItem(it))
            })
            // items.addAll(tempList)
            withContext(Dispatchers.Main) {
                submitList(tempList)
                notifyDataSetChanged()
            }
        }
    }

    fun updateHeaderWithStudyReminderInformation(currentListStudyReminder: VocabularyListStudyReminder?, canShowStudyReminderForDate: Date?) {
        if (items.isNotEmpty()) {
            val header = items.get(0)
            if (header is VocabularyListDetailItem.VocabularyListItem) {
                var changeMade = false
                currentListStudyReminder?.let {
                    header.currentListStudyReminder = it
                    changeMade = true
                }
                canShowStudyReminderForDate?.let {
                    header.canShowStudyReminderForDate = it
                    changeMade = true
                }
                if (changeMade) notifyItemChanged(0)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            LIST_HEADER_ITEM -> ListHeaderViewHolder.from(parent)
            LIST_WORD_ITEM -> VocabularyItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when(holder) {
            is ListHeaderViewHolder -> holder.bind((getItem(position) as VocabularyListDetailItem.VocabularyListItem), listener)
            is VocabularyItemViewHolder -> holder.bind((getItem(position) as VocabularyListDetailItem.WordWithStateItem).item)
            else -> throw ClassCastException("Unknown type of ViewHolder passed ${holder}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is VocabularyListDetailItem.VocabularyListItem -> LIST_HEADER_ITEM
            is VocabularyListDetailItem.WordWithStateItem -> LIST_WORD_ITEM
        }
    }

    class DiffCallBack : DiffUtil.ItemCallback<VocabularyListDetailItem>() {
        override fun areItemsTheSame(oldItem: VocabularyListDetailItem, newItem: VocabularyListDetailItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VocabularyListDetailItem, newItem: VocabularyListDetailItem): Boolean {
            return oldItem == newItem
        }
    }

    class ListHeaderViewHolder private constructor (val binding: VocabularyDetailListHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VocabularyListDetailItem.VocabularyListItem, listener: VocabularyListDetailAdapterListener) {
            binding.listWithWords = item.list
            binding.listener = listener
            binding.canShowStudyReminderForDate = item.canShowStudyReminderForDate
            binding.currentStudyReminder = item.currentListStudyReminder
            binding.listComplete = item.wordListIsComplete
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup) : ListHeaderViewHolder {
                val binding = DataBindingUtil.inflate<VocabularyDetailListHeaderBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.vocabulary_detail_list_header,
                        parent,
                        false
                )
                return ListHeaderViewHolder(binding)
            }
        }
    }

    class VocabularyItemViewHolder private constructor(val binding: VocabularyDetailWordWithStateItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WordWithState) {
            binding.wordWithState = item
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup) : VocabularyItemViewHolder {
                val binding = DataBindingUtil.inflate<VocabularyDetailWordWithStateItemBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.vocabulary_detail_word_with_state_item,
                        parent,
                        false
                )
                return VocabularyItemViewHolder(binding)
            }
        }
    }
}


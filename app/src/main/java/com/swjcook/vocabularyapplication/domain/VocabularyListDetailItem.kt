package com.swjcook.vocabularyapplication.domain

import java.util.*

sealed class VocabularyListDetailItem {
    data class VocabularyListItem(val list: VocabularyList,
          var currentListStudyReminder: VocabularyListStudyReminder? = null,
          var canShowStudyReminderForDate: Date? = null,
          var wordListIsComplete: Boolean = false) : VocabularyListDetailItem() {
        override val id = list._id
    }

    data class WordWithStateItem(val item: WordWithState) : VocabularyListDetailItem() {
        override val id = item.word._id
    }

    abstract val id: String
}
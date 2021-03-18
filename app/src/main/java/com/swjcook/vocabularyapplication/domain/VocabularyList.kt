package com.swjcook.vocabularyapplication.domain

import androidx.room.Embedded
import androidx.room.Relation
import com.swjcook.vocabularyapplication.data.entities.VocabularyListDTO
import com.swjcook.vocabularyapplication.data.entities.VocabularyListStateDTO
import com.swjcook.vocabularyapplication.data.entities.VocabularyListStudyReminderDTO
import com.swjcook.vocabularyapplication.data.entities.WordWithStateDTO
import java.util.*

data class VocabularyList(
        val _id: String = UUID.randomUUID().toString(),
        val title: String,
        val level: String,
        val languageTaught: String,
        val translationLanguage: String,
        var createdAt: Date = Date(System.currentTimeMillis()),
        var updatedAt: Date = Date(System.currentTimeMillis())
)

data class VocabularyListWithWords(
        val list: VocabularyList,
        val words: List<Word>
)

data class VocabularyListWithState(
        val list: VocabularyList,
        val state: VocabularyListState?
)

fun VocabularyList.asDataTransferObject() : VocabularyListDTO {
    return VocabularyListDTO(
            _id = _id,
            title = title,
            level = level,
            languageTaught = languageTaught,
            translationLanguage = translationLanguage,
            createdAt = createdAt,
            updatedAt = updatedAt
    )
}
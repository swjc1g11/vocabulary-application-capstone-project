package com.swjcook.vocabularyapplication.data.entities

import androidx.room.*
import com.swjcook.vocabularyapplication.domain.VocabularyList
import com.swjcook.vocabularyapplication.domain.VocabularyListWithState
import com.swjcook.vocabularyapplication.domain.VocabularyListWithWords
import java.util.*

@Entity(tableName = "vocabulary_lists")
data class VocabularyListDTO(
    @PrimaryKey() val _id: String = UUID.randomUUID().toString(),
    val title: String,
    val level: String,
    val languageTaught: String,
    val translationLanguage: String,
    var createdAt: Date = Date(System.currentTimeMillis()),
    var updatedAt: Date = Date(System.currentTimeMillis())
)

data class VocabularyListWithStateDTO(
    @Embedded val listDTO: VocabularyListDTO,
    @Relation(
        parentColumn = "_id",
        entityColumn = "listId"
    )
    val stateDTO: VocabularyListStateDTO?
)

data class VocabularyListWithWordsDTO(
    @Embedded val listDTO: VocabularyListDTO,
    @Relation(
        parentColumn = "_id",
        entityColumn = "listId"
    )
    val wordDTOS: List<WordDTO>
)


fun VocabularyListDTO.asDomainVocabularyList(): VocabularyList {
    return VocabularyList(
            _id = this._id,
            title = this.title,
            level = this.level,
            languageTaught = this.languageTaught,
            translationLanguage = this.translationLanguage,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
    )
}

fun List<VocabularyListDTO>.asDomainVocabularyLists(): List<VocabularyList> {
    return map {
        it.asDomainVocabularyList()
    }
}

fun VocabularyListWithWordsDTO.asDomainVocabularyListWithWords(): VocabularyListWithWords {
    return VocabularyListWithWords(
            list = listDTO.asDomainVocabularyList(),
            words = wordDTOS.asDomainWords()
    )
}

fun List<VocabularyListWithWordsDTO>.asDomainVocabularyListsWithWords(): List<VocabularyListWithWords> {
    return map {
        it.asDomainVocabularyListWithWords()
    }
}

fun VocabularyListWithStateDTO.asDomainModel(): VocabularyListWithState {
    return VocabularyListWithState(
        list = listDTO.asDomainVocabularyList(),
        state = stateDTO?.asDomainModel()
    )
}

fun List<VocabularyListWithStateDTO>.asDomainModels(): List<VocabularyListWithState> {
    return map {
        it.asDomainModel()
    }
}

data class VocabularyListDetailsDTO(
        @Embedded val listDTO: VocabularyListDTO,
        @Relation(
                parentColumn = "_id",
                entityColumn = "listId"
        )
        val reminderDTO: VocabularyListStudyReminderDTO?,
        @Relation(
                parentColumn = "_id",
                entityColumn = "listId"
        )
        val words: List<WordDTO>
//        @Relation(
//            parentColumn = "_id",
//            entityColumn = "listId"
//        )
//        val wordsState: List<UserWordStateDTO>
)
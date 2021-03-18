package com.swjcook.vocabularyapplication.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.swjcook.vocabularyapplication.domain.Word
import com.swjcook.vocabularyapplication.domain.WordWithState
import java.util.*

@Entity(tableName = "words")
data class WordDTO(
    @PrimaryKey() val _id: String = UUID.randomUUID().toString(),
    val listId: String = "",
    val languageTaught: String,
    val translationLanguage: String,
    val word: String,
    val translation: String,
    var createdAt: Date = Date(System.currentTimeMillis()),
    var updatedAt: Date = Date(System.currentTimeMillis())
)


data class WordWithStateDTO(
    @Embedded val wordDTO: WordDTO,
    @Relation(
                parentColumn = "_id",
                entityColumn = "wordId"
    )
    var stateDTO: UserWordStateDTO?
)


fun WordDTO.asDomainWord(): Word {
    return Word (
            _id = _id,
            listId = listId,
            languageTaught = languageTaught,
            translationLanguage = translationLanguage,
            word = word,
            translation = translation,
            createdAt = createdAt,
            updatedAt = updatedAt
    )
}

fun List<WordDTO>.asDomainWords(): List<Word> {
    return map {
        it.asDomainWord()
    }
}

fun WordWithStateDTO.asDomainWordWithState(): WordWithState {
    return WordWithState(
            word = wordDTO.asDomainWord(),
            state = stateDTO?.asDomainUserWordState()
    )
}

fun List<WordWithStateDTO>.asDomainWordsWithState(): List<WordWithState> {
    return map {
        it.asDomainWordWithState()
    }
}
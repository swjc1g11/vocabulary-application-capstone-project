package com.swjcook.vocabularyapplication.domain

import com.swjcook.vocabularyapplication.data.entities.WordDTO
import java.util.*

data class Word(
        val _id: String = UUID.randomUUID().toString(),
        val listId: String = "",
        val languageTaught: String,
        val translationLanguage: String,
        val word: String,
        val translation: String,
        var createdAt: Date = Date(System.currentTimeMillis()),
        var updatedAt: Date = Date(System.currentTimeMillis())
)

data class WordWithState(
        val word: Word,
        var state: UserWordState?
)

fun Word.asDataTransferObject(): WordDTO {
    return WordDTO(
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

fun List<Word>.asDataTransferObjects(): List<WordDTO> {
    return map {
        it.asDataTransferObject()
    }
}
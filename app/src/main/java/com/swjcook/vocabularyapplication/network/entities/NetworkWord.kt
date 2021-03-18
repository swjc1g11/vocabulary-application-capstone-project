package com.swjcook.vocabularyapplication.network.entities

import com.squareup.moshi.JsonClass
import com.swjcook.vocabularyapplication.data.entities.WordDTO
import com.swjcook.vocabularyapplication.domain.Word
import com.swjcook.vocabularyapplication.network.Network
import java.util.*

@JsonClass(generateAdapter = true)
data class NetworkWord(
    val _id: String = UUID.randomUUID().toString(),
    val listId: String = "",
    val languageTaught: String,
    val translationLanguage: String,
    val word: String,
    val translation: String
)

fun NetworkWord.asDomainModel() : Word {
    return Word(
        _id = _id,
        listId = listId,
        languageTaught = languageTaught,
        translationLanguage = translationLanguage,
        word = word,
        translation = translation
    )
}

fun List<NetworkWord>.asDomainModels() : List<Word> {
    return map {
        it.asDomainModel()
    }
}

fun NetworkWord.asDataTransferObject(): WordDTO {
    return WordDTO(
        _id = _id,
        listId = listId,
        languageTaught = languageTaught,
        translationLanguage = translationLanguage,
        word = word,
        translation = translation
    )
}

fun List<NetworkWord>.asDataTransferObjects(): List<WordDTO> {
    return map {
        it.asDataTransferObject()
    }
}
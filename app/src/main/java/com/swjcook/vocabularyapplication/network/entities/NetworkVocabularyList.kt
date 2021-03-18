package com.swjcook.vocabularyapplication.network.entities

import com.squareup.moshi.JsonClass
import com.swjcook.vocabularyapplication.data.entities.VocabularyListDTO
import com.swjcook.vocabularyapplication.domain.VocabularyList
import com.swjcook.vocabularyapplication.network.Network
import java.util.*

@JsonClass(generateAdapter = true)
data class NetworkVocabularyList(
    val _id: String = UUID.randomUUID().toString(),
    val title: String,
    val level: String,
    val languageTaught: String,
    val translationLanguage: String
)

fun NetworkVocabularyList.asDomainModel(): VocabularyList {
    return VocabularyList(
        _id = _id,
        title = title,
        level = level,
        languageTaught = languageTaught,
        translationLanguage = translationLanguage
    )
}

fun List<NetworkVocabularyList>.asDomainModels(): List<VocabularyList> {
    return map {
        it.asDomainModel()
    }
}

fun NetworkVocabularyList.asDataTransferObject() : VocabularyListDTO {
    return VocabularyListDTO(
        _id = _id,
        title = title,
        level = level,
        languageTaught = languageTaught,
        translationLanguage = translationLanguage
    )
}

fun List<NetworkVocabularyList>.asDataTransferObjects() : List<VocabularyListDTO> {
    return map {
        it.asDataTransferObject()
    }
}
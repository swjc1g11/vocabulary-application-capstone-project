package com.swjcook.vocabularyapplication.domain

import com.swjcook.vocabularyapplication.data.entities.VocabularyListDTO
import com.swjcook.vocabularyapplication.data.entities.VocabularyListStateDTO
import java.util.*

enum class VocabularyListStatus(val status: String) {
    NOT_STARTED("not_started"),
    IN_PROGRESS("in_progress"),
    ACQUIRED("acquired")
}

data class VocabularyListState(
    val _id: String = UUID.randomUUID().toString(),
    val listId : String = "",
    val status: VocabularyListStatus = VocabularyListStatus.NOT_STARTED,
    var createdAt: Date = Date(System.currentTimeMillis()),
    var updatedAt: Date = Date(System.currentTimeMillis())
)

fun VocabularyListState.asDataTransferObject(): VocabularyListStateDTO {
    return VocabularyListStateDTO(
        _id = _id,
        listId = listId,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
package com.swjcook.vocabularyapplication.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.swjcook.vocabularyapplication.domain.VocabularyListState
import com.swjcook.vocabularyapplication.domain.VocabularyListStatus
import java.util.*

@Entity(tableName = "vocabulary_list_state")
data class VocabularyListStateDTO(
    @PrimaryKey() val _id: String = UUID.randomUUID().toString(),
    val listId : String = "",
    val status: VocabularyListStatus = VocabularyListStatus.NOT_STARTED,
    var createdAt: Date = Date(System.currentTimeMillis()),
    var updatedAt: Date = Date(System.currentTimeMillis())
)

fun VocabularyListStateDTO.asDomainModel(): VocabularyListState {
    return VocabularyListState(
        _id = _id,
        listId = listId,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<VocabularyListStateDTO>.asDomainModels(): List<VocabularyListState> {
    return map {
        it.asDomainModel()
    }
}
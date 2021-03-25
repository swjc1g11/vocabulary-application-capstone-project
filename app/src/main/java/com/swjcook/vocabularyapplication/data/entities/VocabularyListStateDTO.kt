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
    val userId: String = "",
    var practiceInterval : UserWordStateLevel = UserWordStateLevel.LEVEL_ONE,
    var lastChangeInInterval: Date = Date(System.currentTimeMillis()),
    var nextChangeInIntervalPossibleOn: Date? = null,
    var listAcquired : Boolean = false,
    var createdAt: Date = Date(System.currentTimeMillis()),
    var updatedAt: Date = Date(System.currentTimeMillis())
)

fun VocabularyListStateDTO.asDomainModel(): VocabularyListState {
    return VocabularyListState(
        _id = _id,
        listId = listId,
        practiceInterval = practiceInterval,
        lastChangeInInterval = lastChangeInInterval,
        nextChangeInIntervalPossibleOn = nextChangeInIntervalPossibleOn,
        listAcquired = listAcquired,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<VocabularyListStateDTO>.asDomainModels(): List<VocabularyListState> {
    return map {
        it.asDomainModel()
    }
}
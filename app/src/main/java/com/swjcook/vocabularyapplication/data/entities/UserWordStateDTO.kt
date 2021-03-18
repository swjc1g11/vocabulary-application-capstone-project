package com.swjcook.vocabularyapplication.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.swjcook.vocabularyapplication.domain.UserWordState
import java.util.*

enum class UserWordStateLevel(val practiceInterval: Long, val levelKey: Int) {
    UNKNOWN(0, 0),
    LEVEL_ONE(1000 * 60 * 60 * 24, 1),
    LEVEL_TWO(LEVEL_ONE.practiceInterval * 2, 2),
    LEVEL_THREE(LEVEL_ONE.practiceInterval * 7, 3),
    LEVEL_FOUR(LEVEL_THREE.practiceInterval *  2, 4),
    LEVEL_FIVE(LEVEL_FOUR.practiceInterval * 2, 5);

    fun next() = when (this) {
        UNKNOWN -> UNKNOWN
        LEVEL_ONE -> LEVEL_TWO
        LEVEL_TWO -> LEVEL_THREE
        LEVEL_THREE -> LEVEL_FOUR
        LEVEL_FOUR -> LEVEL_FIVE
        LEVEL_FIVE -> LEVEL_FIVE
    }
}

@Entity(tableName = "user_word_state")
data class UserWordStateDTO(
        @PrimaryKey() val _id: String = UUID.randomUUID().toString(),
        val wordId: String = "",
        val listId: String = "",
        val userId: String = "",
        var questionsAnswered: Int = 0,
        var score : Int = 0,
        var practiceInterval : UserWordStateLevel = UserWordStateLevel.LEVEL_ONE,
        var lastChangeInInterval: Date = Date(System.currentTimeMillis()),
        var nextChangeInIntervalPossibleOn: Date? = null,
        var wordAcquired : Boolean = false,
        var createdAt: Date = Date(System.currentTimeMillis()),
        var updatedAt: Date = Date(System.currentTimeMillis())
)

// UPDATE TO USE INTERVAL AND INTERVAL SORE PRINCIPLE FOR SPACED REPETITION?

fun UserWordStateDTO.asDomainUserWordState(): UserWordState {
    return UserWordState(
            _id = _id,
            wordId = wordId,
            listId = listId,
            userId = userId,
            questionsAnswered = questionsAnswered,
            score = score,
            practiceInterval = practiceInterval,
            lastChangeInInterval = lastChangeInInterval,
            nextChangeInIntervalPossibleOn = nextChangeInIntervalPossibleOn,
            wordAcquired = wordAcquired,
            createdAt = createdAt,
            updatedAt = updatedAt
    )
}

fun List<UserWordStateDTO>.asDomainUserWordStates(): List<UserWordState> {
    return map {
        it.asDomainUserWordState()
    }
}
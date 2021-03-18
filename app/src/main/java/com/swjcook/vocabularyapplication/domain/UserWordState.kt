package com.swjcook.vocabularyapplication.domain

import com.swjcook.vocabularyapplication.data.entities.UserWordStateDTO
import com.swjcook.vocabularyapplication.data.entities.UserWordStateLevel
import java.util.*

data class UserWordState(
        val _id: String = UUID.randomUUID().toString(),
        val wordId: String = "",
        val listId: String = "",
        val userId: String = "",
        var questionsAnswered: Int = 0,
        var score: Int = 0,
        var practiceInterval: UserWordStateLevel = UserWordStateLevel.LEVEL_ONE,
        var lastChangeInInterval: Date = Date(System.currentTimeMillis()),
        var nextChangeInIntervalPossibleOn: Date? = null,
        var wordAcquired : Boolean = false,
        var createdAt: Date = Date(System.currentTimeMillis()),
        var updatedAt: Date = Date(System.currentTimeMillis())
) {
    fun checkAndActOnChangeInIntervalIfRequired() {
        // If it is more than 100
        if (score >= 100) {
            // Check the interval at which the user should practice is less than two months (in hours)
            if (practiceInterval != UserWordStateLevel.LEVEL_FIVE) {
                // If there is no date for the next change in interval (only just scored over 100) add one one practice interval from now
                if (nextChangeInIntervalPossibleOn == null) {
                    val calendar = Calendar.getInstance()
                    lastChangeInInterval = Date(calendar.timeInMillis)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.add(Calendar.MILLISECOND, practiceInterval.practiceInterval.toInt())
                    val newChangeInIntervalPossibleOn = Date(
                            calendar.timeInMillis
                    )
                    nextChangeInIntervalPossibleOn = newChangeInIntervalPossibleOn
                }
                // If the practice interval is up
                // Next change in interval is not null due to the above code
                if (Calendar.getInstance().timeInMillis > nextChangeInIntervalPossibleOn!!.time) {
                    // Double the practice interval
                    practiceInterval = practiceInterval.next()
                    // Add the score the user should get for this question
                    score = 25
                    // Set the nextChangeInInterval possible on to null as the user needs to score 100 once more
                    nextChangeInIntervalPossibleOn = null
                }
            } else {
                if (practiceInterval == UserWordStateLevel.LEVEL_FIVE) {
                    wordAcquired = true
                }
            }
        }
    }
}

fun UserWordState.asDataTransferObject(): UserWordStateDTO {
    return UserWordStateDTO(
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
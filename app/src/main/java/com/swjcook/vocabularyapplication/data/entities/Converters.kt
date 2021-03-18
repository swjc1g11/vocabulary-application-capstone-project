package com.swjcook.vocabularyapplication.data.entities

import androidx.room.TypeConverter
import com.swjcook.vocabularyapplication.domain.VocabularyListStatus
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromVocabularyListStatus(status: VocabularyListStatus): String = status.status

    @TypeConverter
    fun toVocabularyListStatus(status: String): VocabularyListStatus = enumValueOf(status)

    @TypeConverter
    fun fromUserWordStateLevel(level: UserWordStateLevel): Int = level.levelKey

    @TypeConverter
    fun toUserWordStateLevel(levelKey: Int): UserWordStateLevel = when (levelKey) {
        1 -> UserWordStateLevel.LEVEL_ONE
        2 -> UserWordStateLevel.LEVEL_TWO
        3 -> UserWordStateLevel.LEVEL_THREE
        4 -> UserWordStateLevel.LEVEL_FOUR
        5 -> UserWordStateLevel.LEVEL_FIVE
        else -> UserWordStateLevel.LEVEL_ONE
    }
}
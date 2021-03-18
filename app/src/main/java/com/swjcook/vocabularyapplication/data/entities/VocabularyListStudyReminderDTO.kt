package com.swjcook.vocabularyapplication.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.swjcook.vocabularyapplication.domain.VocabularyListStudyReminder
import java.util.*


@Entity(tableName = "vocabulary_list_study_reminders", indices = [Index(value = ["_id", "listId"], unique = true)])
data class VocabularyListStudyReminderDTO(
        @PrimaryKey(autoGenerate = true) val _id : Long = 0L,
        val listId: String,
        val userId: String,
        val showReminderOn: Date
)

fun VocabularyListStudyReminderDTO.asDomainModel() : VocabularyListStudyReminder {
    return VocabularyListStudyReminder(
            _id = _id,
            listId = listId,
            userId = userId,
            showReminderOn = showReminderOn
    )
}
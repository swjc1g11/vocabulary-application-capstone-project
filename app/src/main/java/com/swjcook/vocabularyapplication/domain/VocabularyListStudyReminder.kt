package com.swjcook.vocabularyapplication.domain

import android.os.Parcelable
import com.swjcook.vocabularyapplication.data.entities.VocabularyListStudyReminderDTO
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class VocabularyListStudyReminder(
        val _id: Long = 0L,
        val listId: String,
        val userId: String,
        val showReminderOn: Date
) : Parcelable

fun VocabularyListStudyReminder.asDataTransferObject() : VocabularyListStudyReminderDTO {
    return VocabularyListStudyReminderDTO(
            _id = _id,
            listId = listId,
            userId = userId,
            showReminderOn = showReminderOn
    )
}
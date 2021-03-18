package com.swjcook.vocabularyapplication.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swjcook.vocabularyapplication.data.entities.VocabularyListStudyReminderDTO

@Dao
interface VocabularyListStudyReminderDao {

    @Query("SELECT * FROM vocabulary_list_study_reminders WHERE listId = :listId")
    fun getStudyReminderByListId(listId: String): LiveData<VocabularyListStudyReminderDTO?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOne(studyReminderDTO: VocabularyListStudyReminderDTO): Long?

    @Query("DELETE FROM vocabulary_list_study_reminders WHERE listId = :listId")
    suspend fun deleteByListId(listId: String)
}
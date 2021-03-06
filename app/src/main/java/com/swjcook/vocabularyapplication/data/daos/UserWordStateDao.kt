package com.swjcook.vocabularyapplication.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.swjcook.vocabularyapplication.data.entities.UserWordStateDTO
import java.util.*

@Dao
interface UserWordStateDao {

    @Query("SELECT * FROM user_word_state WHERE _id = :stateId LIMIT 1")
    suspend fun getStateById(stateId: String): UserWordStateDTO?

    @Query("SELECT * FROM user_word_state WHERE wordId == :wordId LIMIT 1")
    suspend fun getStateByWordId(wordId: String): UserWordStateDTO?

    @Query("SELECT * FROM user_word_state WHERE listId = :listId AND userId = :userId")
    fun getStateByListAndUserId(listId: String, userId: String): LiveData<List<UserWordStateDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(stateDTO: UserWordStateDTO): Long

    @Update
    suspend fun updateOne(stateDTO: UserWordStateDTO)

    @Update
    suspend fun updateMany(stateDTOs: List<UserWordStateDTO>)

    @Query("""UPDATE user_word_state 
        SET nextChangeInIntervalPossibleOn = null, score = 0,
        practiceInterval = (CASE WHEN practiceInterval < 5 THEN practiceInterval + 1 ELSE practiceInterval END)
        WHERE nextChangeInIntervalPossibleOn < :currentDateInMilliseconds""")
    fun readjustPracticeIntervalsMetToday(currentDateInMilliseconds: Long)

    @Query("""
        UPDATE user_word_state
        SET nextChangeInIntervalPossibleOn = :nextChangeInIntervalPossibleOn, lastChangeInInterval = :lastChangeInIntervalOn, wordAcquired = :wordAcquired
        WHERE wordId IN (:ids) AND userId = :userId
    """)
    fun updateUserWordStateForRangeOfWords(
            ids: List<String>,
            nextChangeInIntervalPossibleOn: Date?,
            lastChangeInIntervalOn: Date?,
            wordAcquired: Boolean,
            userId: String)


}
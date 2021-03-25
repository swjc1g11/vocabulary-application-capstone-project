package com.swjcook.vocabularyapplication.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.swjcook.vocabularyapplication.data.entities.*

@Dao
interface VocabularyListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(listDTO: VocabularyListDTO): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertState(vocabularyListStateDTO: VocabularyListStateDTO): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(vararg listDTOS: VocabularyListDTO)

    @Update
    suspend fun updateState(vocabularyListStateDTO: VocabularyListStateDTO)


    @Query("SELECT * FROM vocabulary_lists WHERE _id = :id")
    fun findById(id: String): LiveData<VocabularyListDTO>

    @Query("SELECT * FROM vocabulary_lists WHERE _id = :id")
    fun findByIdNoLiveData(id: String): VocabularyListDTO

    @Transaction
    @Query("SELECT * FROM vocabulary_lists WHERE _id = :id")
    fun findByIdVocabularyListDetail(id: String): LiveData<VocabularyListDetailsDTO?>

    @Query("SELECT * FROM vocabulary_lists")
    fun findAll(): LiveData<List<VocabularyListDTO>>

    @Query("SELECT * FROM vocabulary_list_state WHERE userId = :userId")
    fun findAllState(userId: String): LiveData<List<VocabularyListStateDTO>>

    @Transaction
    @Query("SELECT * FROM vocabulary_lists")
    fun findAllWithState(): LiveData<List<VocabularyListWithStateDTO>>

    @Transaction
    @Query("SELECT * FROM vocabulary_lists WHERE _id = :id")
    fun findByIdWithWords(id: String): LiveData<VocabularyListWithWordsDTO>

    @Transaction
    @Query("SELECT * FROM vocabulary_lists WHERE _id = :id")
    fun findByIdWithWordsNoLiveData(id: String): VocabularyListWithWordsDTO?

    @Transaction
    @Query("SELECT * FROM vocabulary_lists WHERE _id = :id")
    fun findByIdWithStateNoLiveData(id: String): VocabularyListWithStateDTO?

    @Transaction
    @Query("SELECT * FROM vocabulary_lists")
    fun findAllWithWords(): LiveData<List<VocabularyListWithWordsDTO>>

    @Query("""UPDATE vocabulary_list_state
        SET nextChangeInIntervalPossibleOn = null, practiceInterval = (CASE WHEN practiceInterval < 5 THEN practiceInterval + 1 ELSE practiceInterval END)
        WHERE nextChangeInIntervalPossibleOn < :currentDateInMilliseconds
    """)
    fun readjustPracticeIntervalsMetTodayVocabList(currentDateInMilliseconds: Long)
}


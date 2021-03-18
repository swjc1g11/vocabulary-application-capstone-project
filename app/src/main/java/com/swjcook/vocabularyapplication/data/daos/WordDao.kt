package com.swjcook.vocabularyapplication.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.swjcook.vocabularyapplication.data.entities.WordDTO
import com.swjcook.vocabularyapplication.data.entities.WordWithStateDTO

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(wordDTO: WordDTO): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(vararg wordDTOS: WordDTO)

    @Query("SELECT * FROM words WHERE _id = :id")
    fun findById(id: String): LiveData<WordDTO>

    @Query("SELECT * FROM words WHERE listId = :listId")
    fun findByListId(listId: String): LiveData<List<WordDTO>>

    @Transaction
    @Query("SELECT * FROM words WHERE _id = :id")
    fun findByIdWithState(id: String): LiveData<WordWithStateDTO>

    @Transaction
    @Query("SELECT * FROM words WHERE _id IN (:ids)")
    fun findAllWithState(ids: List<String>): LiveData<List<WordWithStateDTO>>

    @Transaction
    @Query("SELECT * FROM words WHERE listId = :listId")
    fun findAllWithStateByListId(listId: String): LiveData<List<WordWithStateDTO>>

    @Transaction
    @Query("SELECT * FROM words WHERE listId = :listId")
    fun findAllWithStateByListIdNoLiveData(listId: String): List<WordWithStateDTO>
}
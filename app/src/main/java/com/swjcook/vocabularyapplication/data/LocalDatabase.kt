package com.swjcook.vocabularyapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.swjcook.vocabularyapplication.data.daos.UserWordStateDao
import com.swjcook.vocabularyapplication.data.daos.VocabularyListDao
import com.swjcook.vocabularyapplication.data.daos.VocabularyListStudyReminderDao
import com.swjcook.vocabularyapplication.data.daos.WordDao
import com.swjcook.vocabularyapplication.data.entities.*

// https://github.com/swjc1g11/kotlintodoapplication/blob/main/app/src/main/java/com/swjcook/kotlintodoapplication/data/ProjectWithToDos.kt

@Database(entities = [
    VocabularyListDTO::class,
    VocabularyListStateDTO::class,
    WordDTO::class,
    UserWordStateDTO::class,
    VocabularyListStudyReminderDTO::class
], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract val vocabularyListDao: VocabularyListDao
    abstract val wordDao: WordDao
    abstract val userWordStateDao: UserWordStateDao
    abstract val studyReminderDao: VocabularyListStudyReminderDao

    companion object {
        @Volatile
        private lateinit var INSTANCE : LocalDatabase

        fun getDatabase(context: Context) : LocalDatabase {
            synchronized(LocalDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        LocalDatabase::class.java,
                        "vocabulary_application").build()
                }
            }
            return INSTANCE
        }
    }
}
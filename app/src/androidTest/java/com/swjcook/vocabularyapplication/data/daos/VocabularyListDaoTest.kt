package com.swjcook.vocabularyapplication.data.daos

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.swjcook.vocabularyapplication.data.LocalDatabase
import com.swjcook.vocabularyapplication.data.entities.VocabularyListDTO
import com.swjcook.vocabularyapplication.data.entities.VocabularyListWithWordsDTO
import com.swjcook.vocabularyapplication.data.entities.WordDTO
import com.swjcook.vocabularyapplication.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class VocabularyListDaoTest {

    companion object {
        const val TAG = "LocalVocabListDaoTest"
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: LocalDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            LocalDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertListNoWords_checkValid() = runBlockingTest {
        val list = VocabularyListDTO(
            title = "Test List...",
            level = "A2.1",
            languageTaught = "de-DE",
            translationLanguage = "en-UK"
        )

        val listId = database.vocabularyListDao.insertOne(list)
        val result = database.vocabularyListDao.findByIdWithWords(listId)
        val value: VocabularyListWithWordsDTO = result.getOrAwaitValue()

        Log.d(TAG, value.toString())

        assertThat("Returned id is valid", listId, `is`(greaterThan(-1L)))
        assertThat("Result value is not null", value, notNullValue())
        assertThat("Title is correct", value.listDTO.title, `is`(list.title))
        assertThat("Level is correct", value.listDTO.level, `is`(list.level))
        assertThat("Language taught is correct", value.listDTO.languageTaught, `is`(list.languageTaught))
        assertThat("Translation language is correct", value.listDTO.translationLanguage, `is`(list.translationLanguage))
        assertThat("List is not associated with words", value.wordDTOS.isEmpty(), `is`(true))
    }

    @Test
    fun insertListWithWords_checkValid() = runBlockingTest {
        val list = VocabularyListDTO(
            title = "Test List 2",
            level = "B1.1",
            languageTaught = "de-DE",
            translationLanguage = "en-UK"
        )

        // Save a list
        val listId = database.vocabularyListDao.insertOne(list)

        // Save words associated with that list
        val wordOne = WordDTO(listId = listId,  languageTaught = list.languageTaught,
            translationLanguage = list.translationLanguage, word = "ein Ordner", translation = "a folder")
        val wordTwo = WordDTO(listId = listId, languageTaught = list.languageTaught,
            translationLanguage = list.translationLanguage, word = "eine Datei", translation = "a file")
        database.wordDao.insertMany(wordOne, wordTwo)

        // Retrieve the list
        val result = database.vocabularyListDao.findByIdWithWords(listId)
        val value: VocabularyListWithWordsDTO = result.getOrAwaitValue()

        Log.d(TAG, value.toString())

        assertThat("Returned id is valid", listId, `is`(greaterThan(-1L)))
        assertThat("Result value is not null", value, notNullValue())
        assertThat("Title is correct", value.listDTO.title, `is`(list.title))
        assertThat("Level is correct", value.listDTO.level, `is`(list.level))
        assertThat("Language taught is correct", value.listDTO.languageTaught, `is`(list.languageTaught))
        assertThat("Translation language is correct", value.listDTO.translationLanguage, `is`(list.translationLanguage))
        assertThat("Words is not empty", value.wordDTOS.isNotEmpty(), `is`(true))
    }

}


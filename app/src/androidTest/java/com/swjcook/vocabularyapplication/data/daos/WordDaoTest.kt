package com.swjcook.vocabularyapplication.data.daos

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.swjcook.vocabularyapplication.data.LocalDatabase
import com.swjcook.vocabularyapplication.data.entities.UserWordStateDTO
import com.swjcook.vocabularyapplication.data.entities.WordDTO
import com.swjcook.vocabularyapplication.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.*
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WordDaoTest {
    companion object {
        const val TAG = "WordDaoTest"
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: LocalDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LocalDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertOne_checkValid() = runBlockingTest {
        val word = WordDTO(languageTaught = "de-DE", translationLanguage = "en-UK", word = "ein Ordner", translation = "a folder")

        val insertedId = database.wordDao.insertOne(word)
        val result = database.wordDao.findById(insertedId)
        val value = result.getOrAwaitValue()

        assertThat("Id greater than zero", insertedId, `is`(greaterThan(-1L)))
        assertThat("_id is correct", value._id, `is`(value._id))
        assertThat("languageTaught is correct", value.languageTaught, `is`(word.languageTaught))
        assertThat("translationLanguage is correct", value.translationLanguage, `is`(word.translationLanguage))
        assertThat("word is correct ", value.word, `is`(word.word))
        assertThat("translation is correct", value.translation, `is`(word.translation))
    }

    // Test get words with state
    // Also add state dao test - update state and insert state - check update on conflict abort - how to figure out if aborts?
    // test for abort on insert duplicate...

    @Test
    fun insertState_getWordWithStateNotNull() = runBlockingTest {
        val word = WordDTO(languageTaught = "de-DE", translationLanguage = "en-UK", word = "eine Datei", translation = "a file")
        val insertedId = database.wordDao.insertOne(word)

        val wordState = UserWordStateDTO(wordId = insertedId, userId = "1234", questionsAnswered = 2)
        val insertedWordStateId = database.userWordStateDao.insertOne(wordState)

        val result = database.wordDao.findByIdWithState(insertedId)
        val value = result.getOrAwaitValue()

        Log.d(TAG, value.toString())

        assertThat("Word exists", value, notNullValue())
        assertThat("State is present for word", value.stateDTO, notNullValue())
        assertThat("State number of questions correct", value.stateDTO?.questionsAnswered, `is`(wordState.questionsAnswered))
    }

    @Test
    fun insertWord_getWordWithStateNull() = runBlockingTest {
        val word = WordDTO(languageTaught = "de-DE", translationLanguage = "en-UK", word = "hochladen", translation = "upload")
        val insertedId = database.wordDao.insertOne(word)

        val result = database.wordDao.findByIdWithState(insertedId)
        val value = result.getOrAwaitValue()

        Log.d(TAG, value.toString())

        assertThat("Word exists", value, notNullValue())
        assertThat("State for word is null", value.stateDTO, `is`(nullValue()))
    }

    @Test
    fun updateWordState_checkUpdateIsValid() = runBlockingTest {
        val word = WordDTO(languageTaught = "de-DE", translationLanguage = "en-UK", word = "erstellen", translation = "create")
        val insertedId = database.wordDao.insertOne(word)

        val wordState = UserWordStateDTO(wordId = insertedId, userId = "1234", questionsAnswered = 2)
        val insertedWordStateId = database.userWordStateDao.insertOne(wordState)

        var result = database.wordDao.findByIdWithState(insertedId)
        var value = result.getOrAwaitValue()

        assertThat("State is not null", value.stateDTO, notNullValue())
        val state = value.stateDTO!!

        state.questionsAnswered++
        database.userWordStateDao.updateOne(state)

        result = database.wordDao.findByIdWithState(insertedId)
        value = result.getOrAwaitValue()

        assertThat("State is not null after update", value.stateDTO, notNullValue())
        assertThat("Number of questions answered is three", value.stateDTO?.questionsAnswered, `is`(3))
    }
}
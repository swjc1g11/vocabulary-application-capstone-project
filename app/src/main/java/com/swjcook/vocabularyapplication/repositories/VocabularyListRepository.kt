package com.swjcook.vocabularyapplication.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.swjcook.vocabularyapplication.data.LocalDatabase
import com.swjcook.vocabularyapplication.data.entities.*
import com.swjcook.vocabularyapplication.domain.*
import com.swjcook.vocabularyapplication.network.Network
import com.swjcook.vocabularyapplication.network.entities.asDataTransferObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class VocabularyListRepository(val database: LocalDatabase, val network: Network) {
    // TODO incorporate user id into dao methods and into repository so user state is segmented by user

    fun getAllVocabularyListsWithState(): LiveData<List<VocabularyListWithState>> {
        val result = database.vocabularyListDao.findAllWithState()
        return Transformations.map(result) {
            it.asDomainModels()
        }
    }

    fun getVocabularyListDetailsMediated(listId: String, userId: String): LiveData<List<VocabularyListDetailItem>?> {
        val initialDataSet = this.getVocabularyListDetailsById(listId, userId)
        val userStates = database.userWordStateDao.getStateByListAndUserId(listId, userId)

        val result = MediatorLiveData<List<VocabularyListDetailItem>?>()

        result.addSource(initialDataSet) { value ->
            result.value = combineVocabularyListDetailItemAndUserStateDTOLiveData(initialDataSet, userStates)
        }

        result.addSource(userStates) { value ->
            result.value = combineVocabularyListDetailItemAndUserStateDTOLiveData(initialDataSet, userStates)
        }

        return result
    }

    fun getWordsWithStateMediated(listId: String, userId: String) : LiveData<List<WordWithState>?> {
        val liveData1 = database.wordDao.findByListId(listId)
        val liveData2 = database.userWordStateDao.getStateByListAndUserId(listId, userId)

        val result = MediatorLiveData<List<WordWithState>?>()

        result.addSource(liveData1) { value ->
            result.value = combineWordDTOAndUserStateDTOLiveData(liveData1, liveData2)
        }
        result.addSource(liveData2) { value ->
            result.value = combineWordDTOAndUserStateDTOLiveData(liveData1, liveData2)
        }
        return result
    }

    private fun combineVocabularyListDetailItemAndUserStateDTOLiveData(
            items: LiveData<List<VocabularyListDetailItem>>, userStates: LiveData<List<UserWordStateDTO>>) : List<VocabularyListDetailItem>? {
        val itemsValue = items.value
        val userStatesValue = userStates.value

        if (itemsValue == null || userStatesValue == null) {
            return null
        }

        var lowestDate: Date? = null
        val stateMap = mutableMapOf<String, UserWordState>()
        var wordsNotAcquired = itemsValue.size - 1
        userStatesValue.forEach {
            stateMap.put(it.wordId, it.asDomainUserWordState())
            if (lowestDate == null) lowestDate = it.nextChangeInIntervalPossibleOn
            if (it.wordAcquired == true) wordsNotAcquired--
            it.nextChangeInIntervalPossibleOn?.let { date ->
                if (date.time < lowestDate!!.time) lowestDate == date
            }
        }

        itemsValue.forEach {
            if (it is VocabularyListDetailItem.WordWithStateItem) {
                val word = it.item.word
                if (stateMap.containsKey(word._id)) {
                    it.item.state = stateMap.get(word._id)
                }
            } else if (it is VocabularyListDetailItem.VocabularyListItem) {
                it.canShowStudyReminderForDate = lowestDate
                it.wordListIsComplete = wordsNotAcquired == 0
            }
        }

        return itemsValue
    }

    private fun combineWordDTOAndUserStateDTOLiveData(words: LiveData<List<WordDTO>>, userStates: LiveData<List<UserWordStateDTO>>): MutableList<WordWithState>? {
        val wordValue = words.value
        val userStatesValue = userStates.value
        // Don't send a success until we have both results
        if (wordValue == null || userStatesValue == null) {
            return null
        }
        val stateMap = mutableMapOf<String, UserWordStateDTO>()
        userStatesValue.forEach {
            stateMap.put(it.wordId, it)
        }
        val tempList = mutableListOf<WordWithState>()
        wordValue.forEach {
            val wordWithState = WordWithState(word = it.asDomainWord(), state = null)
            if (stateMap.containsKey(wordWithState.word._id)) {
                wordWithState.state = stateMap.get(wordWithState.word._id)!!.asDomainUserWordState()
            }
            tempList.add(wordWithState)
        }
        return tempList
    }

    private fun getVocabularyListDetailsById(id: String, userId: String): LiveData<List<VocabularyListDetailItem>> {
        val result =  database.vocabularyListDao.findByIdVocabularyListDetail(id)
        return Transformations.map(result) { listWithDetails ->
            val tempList = mutableListOf<VocabularyListDetailItem>()
            if (listWithDetails != null) {
                listWithDetails.listDTO?.let {
                    val list = listWithDetails.listDTO.asDomainVocabularyList()
                    var lowestDate : Date? = null
                    val headerItem = VocabularyListDetailItem.VocabularyListItem(list, listWithDetails.reminderDTO?.asDomainModel(), lowestDate)
                    tempList.add(headerItem)
                    listWithDetails.words.forEach { word ->
                        val wordWithState = WordWithState(word = word.asDomainWord(), state = null)
                        tempList.add(
                                VocabularyListDetailItem.WordWithStateItem(wordWithState)
                        )
                    }
                }
            }
            tempList
        }
    }

    suspend fun saveVocabularyListStudyReminder(studyReminder: VocabularyListStudyReminder) : Long? {
        val id = database.studyReminderDao.insertOne(studyReminder.asDataTransferObject())
        return id
    }

    suspend fun checkReminderAndDeleteIfExpired(studyReminder: VocabularyListStudyReminder) {
        if (Calendar.getInstance().timeInMillis > studyReminder.showReminderOn.time) {
            deleteVocabularyListStudyReminderByListId(studyReminder.listId)
        }
    }

    suspend fun deleteVocabularyListStudyReminderByListId(listId: String) {
        database.studyReminderDao.deleteByListId(listId)
    }

    suspend fun readjustWordStateIntervalsExpiredToday() {
        val todayInMillis = Calendar.getInstance().timeInMillis
        withContext(Dispatchers.IO) {
            database.userWordStateDao.readjustPracticeIntervalsMetToday(todayInMillis)
        }
    }

    suspend fun saveWordState(wordWithState: WordWithState) {
        // TODO involve user id in request
        // Word has existing state - update the state
        if (wordWithState.state != null) {
            try {
                wordWithState.state!!.questionsAnswered++
                wordWithState.state!!.updatedAt = Date(System.currentTimeMillis())
                // Increment the user score whilst it is less than 100
                if (wordWithState.state!!.score < 100) {
                    wordWithState.state!!.score += 25;
                }
                wordWithState.state!!.checkAndActOnChangeInIntervalIfRequired()
                database.userWordStateDao.updateOne(wordWithState!!.state!!.asDataTransferObject())
            } catch (error: Exception) {}
        } else {
            val wordState = UserWordState(
                    wordId = wordWithState.word._id,
                    listId = wordWithState.word.listId,
                    userId = "",
                    questionsAnswered = 1,
                    score = 25
            )
            try {
                database.userWordStateDao.insertOne(wordState.asDataTransferObject())
                wordWithState.state = database.userWordStateDao.getStateById(wordState._id)?.asDomainUserWordState()
                Log.i("VocabPracticeSa", wordWithState.toString())
            } catch (error: Exception) {}
        }
    }

    suspend fun downloadAllVocabularyLists() {
        withContext(Dispatchers.IO) {
            try {
                val results = network.vocabularyService.getAllLists()
                database.vocabularyListDao.insertMany(*results.asDataTransferObjects().toTypedArray())
            } catch (e: Exception) {}
        }
    }

    suspend fun downloadVocabularyListById(listId: String) {
        withContext(Dispatchers.IO) {
            try {
                val results = network.vocabularyService.getListByUUID(listId)
                database.wordDao.insertMany(*results.asDataTransferObjects().toTypedArray())
            } catch (e: Exception) {}
        }
    }
}

//    fun getUserStateForVocabularyListByListId(id: String, userId: String): LiveData<List<WordWithState>> {
//        val result = database.wordDao.findAllWithStateByListId(id)
//        return Transformations.map(result) {
//            val tempList = mutableListOf<WordWithState>()
//            it.forEach { wordWithStateDTO ->
//                val wordWithState = wordWithStateDTO.asDomainWordWithState()
//                if (wordWithState.state != null) {
//                    if (wordWithState.state!!.userId != userId) {
//                        wordWithState.state = null
//                    }
//                }
//                tempList.add(wordWithState)
//            }
//            tempList
//        }
//    }

//    fun getStudyReminderByListId(id: String): LiveData<VocabularyListStudyReminder> {
//        val result = database.studyReminderDao.getStudyReminderByListId(id)
//        return Transformations.map(result) {
//            if (it != null) it.asDomainModel() else null
//        }
//    }

//    suspend fun getNextReminderDate(wordsWithState: List<WordWithState>?) : Date? {
//        if (wordsWithState == null) return wordsWithState
//        var lowestPracticeOnInterval = Long.MAX_VALUE
//        wordsWithState.forEach({
//            it.state?.nextChangeInIntervalPossibleOn?.let {
//                if (it.time < lowestPracticeOnInterval) {
//                    lowestPracticeOnInterval = it.time
//                }
//            }
//        })
//        if (lowestPracticeOnInterval == Long.MAX_VALUE) {
//            return null
//        }
//        return Date(lowestPracticeOnInterval)
//    }

//    fun getVocabularyListById(id: String): LiveData<VocabularyList> {
//        val result = database.vocabularyListDao.findById(id)
//        return Transformations.map(result) {
//            it.asDomainVocabularyList()
//        }
//    }

//    fun getVocabularyListWithWordsById(id: String): LiveData<VocabularyListWithWords?> {
//        val result = database.vocabularyListDao.findByIdWithWords(id)
//        return Transformations.map(result) {
//            it.asDomainVocabularyListWithWords()
//        }
//    }
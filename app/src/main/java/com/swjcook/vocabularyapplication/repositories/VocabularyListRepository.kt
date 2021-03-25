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

    fun getAllVocabularyListsWithState(): LiveData<List<VocabularyListWithState>> {
        val a = database.vocabularyListDao.findAll()
        val b = database.vocabularyListDao.findAllState("")

        val result = MediatorLiveData<List<VocabularyListWithState>>()

        result.addSource(a) {
            val combined = combineVocabularyLisDTOsAndStateDTOsLiveData(a, b)
            combined?.let {
                result.value = it
            }
        }

        result.addSource(b) {
            val combined = combineVocabularyLisDTOsAndStateDTOsLiveData(a, b)
            combined?.let {
                result.value = it
            }
        }

        return result

        //        val result = database.vocabularyListDao.findAllWithState()
//        return Transformations.map(result) {
//            it.asDomainModels()
//        }
    }

    fun getVocabularyListDetailsMediated(listId: String, userId: String): LiveData<List<VocabularyListDetailItem>?> {
        val initialDataSet = this.getVocabularyListDetailsById(listId, userId)
        val userStates = database.userWordStateDao.getStateByListAndUserId(listId, userId)

        val result = MediatorLiveData<List<VocabularyListDetailItem>?>()

        result.addSource(initialDataSet) { _ ->
            result.value = combineVocabularyListDetailItemAndUserStateDTOLiveData(initialDataSet, userStates)
        }

        result.addSource(userStates) { _ ->
            result.value = combineVocabularyListDetailItemAndUserStateDTOLiveData(initialDataSet, userStates)
        }

        return result
    }

    fun getWordsWithStateMediated(listId: String, userId: String) : LiveData<List<WordWithState>?> {
        val liveData1 = database.wordDao.findByListId(listId)
        val liveData2 = database.userWordStateDao.getStateByListAndUserId(listId, userId)

        val result = MediatorLiveData<List<WordWithState>?>()

        result.addSource(liveData1) { _ ->
            result.value = combineWordDTOAndUserStateDTOLiveData(liveData1, liveData2)
        }
        result.addSource(liveData2) { _ ->
            result.value = combineWordDTOAndUserStateDTOLiveData(liveData1, liveData2)
        }
        return result
    }

    private fun combineVocabularyLisDTOsAndStateDTOsLiveData(
            lists: LiveData<List<VocabularyListDTO>>, userStates: LiveData<List<VocabularyListStateDTO>>) : List<VocabularyListWithState>? {
        val listsValue = lists.value
        val userStatesValue = userStates.value

        if (listsValue == null || userStatesValue == null) {
            return null
        }

        val listMap = hashMapOf<String, VocabularyListWithState>()
        val initialList = mutableListOf<VocabularyListWithState>()
        listsValue.forEach {
            val listWithState = VocabularyListWithState(list = it.asDomainVocabularyList(), state = null)
            initialList.add(listWithState)
            listMap.put(it._id, listWithState)
        }

        userStatesValue.forEach { state ->
            if (listMap.containsKey(state.listId)) {
                val result = listMap.get(state.listId)
                result?.let {
                    it.state = state.asDomainModel()
                }
            }
        }

        return initialList
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
//                listWithDetails.listDTO?.let {
//
//                }
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

    suspend fun reAdjustWordStateIntervalsExpiredToday() {
        val todayInMillis = Calendar.getInstance().timeInMillis
        withContext(Dispatchers.IO) {
            database.userWordStateDao.readjustPracticeIntervalsMetToday(todayInMillis)

            // TODO also run a query to update vocabulary lists.....
        }
    }

    suspend fun reAdjustVocabularyListStateIntervalsExpiredToday() {
        val todayInMillis = Calendar.getInstance().timeInMillis
        withContext(Dispatchers.IO) {
            database.vocabularyListDao.readjustPracticeIntervalsMetTodayVocabList(todayInMillis)
        }
    }

    /**
     * Beast of a method that updates state if no state exists for a word, or else creates state.
     *
     * In updating, it also decided whether a word can be updated to wait for the next interval to progress a levle. It does this based on whether other words in the same list have a score of 100 or above, too.
     *
     * If the word being saved can be updated, the other words in the same list are also updated at the same time.
     */
    suspend fun saveWordState(wordWithState: WordWithState) {
        withContext(Dispatchers.IO) {
            val wordsWithState = database.wordDao.findAllWithStateByListIdNoLiveData(wordWithState.word.listId).asDomainWordsWithState()
            var wordsReadyForNextInterval = true
            var otherWordIds = mutableListOf<String>()
            var otherWordsAcquired = true
            wordsWithState.forEach {
                if (it.word._id != wordWithState.word._id) {
                    var score = it.state?.score ?: 0
                    otherWordIds.add(it.word._id)
                    if (score < 100) {
                        wordsReadyForNextInterval = false
                    }

                    if (it.state == null) otherWordsAcquired = false
                    else if (!it.state!!.wordAcquired) otherWordsAcquired = false
                }
            }

            saveWordState(wordWithState, wordsReadyForNextInterval, otherWordIds)
            // TODO list acquired is not saving correctly
            val currentWordAcquired = wordWithState.state?.wordAcquired ?: false
            saveVocabularyListState(
                    listId = wordWithState.word.listId,
                    nextChangeInIntervalPossibleOn = wordWithState.state?.nextChangeInIntervalPossibleOn,
                    practiceInterval = wordWithState.state?.practiceInterval,
                    lastChangeInInterval = wordWithState.state?.lastChangeInInterval,
                    listAcquired = otherWordsAcquired && currentWordAcquired
            )
        }
    }

    private suspend fun saveWordState(wordWithState: WordWithState, wordsReadyForNextInterval: Boolean, otherWordIds: List<String>, userId: String = "") {
        if (wordWithState.state != null) {
            try {
                wordWithState.state!!.questionsAnswered++
                wordWithState.state!!.updatedAt = Date(System.currentTimeMillis())
                if (wordWithState.state!!.score < 100) {
                    wordWithState.state!!.score += 25;
                }

                if (wordsReadyForNextInterval) {
                    wordWithState.state!!.checkAndActOnChangeInIntervalIfRequired()
                    database.userWordStateDao.updateUserWordStateForRangeOfWords(
                            ids = otherWordIds,
                            nextChangeInIntervalPossibleOn = wordWithState.state!!.nextChangeInIntervalPossibleOn,
                            lastChangeInIntervalOn = wordWithState.state!!.lastChangeInInterval,
                            wordAcquired = wordWithState.state!!.wordAcquired,
                            userId = userId
                    )
                }

                database.userWordStateDao.updateOne(wordWithState.state!!.asDataTransferObject())
            } catch (error: Exception) {}
        } else {
            val wordState = UserWordState(
                    wordId = wordWithState.word._id,
                    listId = wordWithState.word.listId,
                    userId = userId,
                    questionsAnswered = 1,
                    score = 25
            )
            try {
                database.userWordStateDao.insertOne(wordState.asDataTransferObject())
                wordWithState.state = database.userWordStateDao.getStateById(wordState._id)?.asDomainUserWordState()
            } catch (error: Exception) {}
        }
    }

    private suspend fun saveVocabularyListState(listId: String, practiceInterval: UserWordStateLevel?,
                                        nextChangeInIntervalPossibleOn: Date?, lastChangeInInterval: Date?, listAcquired: Boolean,
                                        userId: String = "") {
        val listWithState = database.vocabularyListDao.findByIdWithStateNoLiveData(listId)?.asDomainModel()
        val state = listWithState?.state
        if (state != null) {
            state.nextChangeInIntervalPossibleOn = nextChangeInIntervalPossibleOn
            state.lastChangeInInterval = lastChangeInInterval ?: Calendar.getInstance().time
            practiceInterval?.let {
                state.practiceInterval = practiceInterval
            }
            state.listAcquired = listAcquired
            database.vocabularyListDao.updateState(state.asDataTransferObject())
        } else{
            val newState = VocabularyListState(
                    listId = listId,
                    nextChangeInIntervalPossibleOn = nextChangeInIntervalPossibleOn,
                    lastChangeInInterval = lastChangeInInterval ?: Date(Calendar.getInstance().timeInMillis),
                    listAcquired = listAcquired,
                    userId = userId
            )
            database.vocabularyListDao.insertState(newState.asDataTransferObject())
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
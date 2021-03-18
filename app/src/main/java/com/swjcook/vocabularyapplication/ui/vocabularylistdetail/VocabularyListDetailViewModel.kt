package com.swjcook.vocabularyapplication.ui.vocabularylistdetail

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.*
import com.swjcook.vocabularyapplication.MainActivity
import com.swjcook.vocabularyapplication.data.entities.VocabularyListDetailsDTO
import com.swjcook.vocabularyapplication.domain.*
import com.swjcook.vocabularyapplication.receivers.VocabularyListStudyReminderReceiver
import com.swjcook.vocabularyapplication.repositories.VocabularyListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class VocabularyListDetailViewModel(val application: Application, val repository: VocabularyListRepository) : ViewModel() {

    // The uuid of the list to be displayed on the details screen - required to kick off the whole process
    private val _uuid = MutableLiveData<String>("")
    val uuid : LiveData<String>
        get() = _uuid

    private val _vocabularyListWithDetails = Transformations.switchMap(uuid) {
        // TODO Use user id in request
        // repository.getVocabularyListDetailsById(it, "")
        repository.getVocabularyListDetailsMediated(it, "")
    }
    val vocabularyListWithDetails : LiveData<List<VocabularyListDetailItem>?>
        get() = _vocabularyListWithDetails

    // A value governing whether or not to show the swipe to refresh refreshing animation
    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading : LiveData<Boolean>
        get() = _showLoading

    /**
     * Set the uuid of the list to be displayed in the view of fragment. Setting this will trigger the fetching of the list, its words and associated data.
     */
    fun setListId(id: String) {
        _uuid.value = id
    }

    /**
     *
     */
    fun setStudyReminder(canShowStudyReminderForDate: Date, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = canShowStudyReminderForDate
        calendar.set(Calendar.HOUR, hour)
        calendar.set(Calendar.MINUTE, minute)

        Log.i("VocabPractice", "Date with hours...")
        Log.i("VocabPractice", calendar.time.toString())

        val header = _vocabularyListWithDetails.value?.let {
            if (it.size > 0) it.get(0) else null
        }
        var studyReminder: VocabularyListStudyReminder? = null
        if (header is VocabularyListDetailItem.VocabularyListItem) {
            studyReminder = VocabularyListStudyReminder(
                    listId =  header.list._id,
                    userId = "",
                    showReminderOn = calendar.time
            )
        }

        if (studyReminder != null) {
            viewModelScope.launch {
                val id = repository.saveVocabularyListStudyReminder(studyReminder)
                if (id != null) {
                    val notifyIntent = Intent(application.applicationContext, VocabularyListStudyReminderReceiver::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable(MainActivity.INTENT_EXTRA_NAVIGATE_TO_DETAILS_ID, studyReminder)
                    notifyIntent.putExtra(MainActivity.INTENT_EXTRA_NAVIGATE_TO_DETAILS_ID, bundle)

//                    Log.i("VocabPracticeVM", notifyIntent.toString())
//                    Log.i("VocabPracticeVm", notifyIntent.extras.toString())
//                    Log.i("VocabPracticeVM", studyReminder.toString())

                    val notifyPendingIntent = PendingIntent.getBroadcast(
                            application,
                            id.toInt(),
                            notifyIntent,
                            0
                    )

                    val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    // val time = System.currentTimeMillis() + 5000
                    AlarmManagerCompat.setExactAndAllowWhileIdle(
                            alarmManager,
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            notifyPendingIntent
                    )
                }
            }
        }
    }

    fun checkStudyReminderAndDeleteIfExpired() {
        val header = _vocabularyListWithDetails.value?.let {
            if (it.size > 0) it.get(0) else null
        }
        var studyReminder: VocabularyListStudyReminder? = null
        if (header is VocabularyListDetailItem.VocabularyListItem) {
            studyReminder = header.currentListStudyReminder
            studyReminder?.let {
                viewModelScope.launch {
                    repository.checkReminderAndDeleteIfExpired(studyReminder)
                }
            }
        }
    }

    fun deleteStudyReminder() {
        viewModelScope.launch {
            val header = _vocabularyListWithDetails.value?.let {
                if (it.size > 0) it.get(0) else null
            }
            var studyReminder: VocabularyListStudyReminder? = null
            if (header is VocabularyListDetailItem.VocabularyListItem) {
                studyReminder = header.currentListStudyReminder
            }
            studyReminder?.let { reminder ->
                val notifyIntent = Intent(application.applicationContext, VocabularyListStudyReminderReceiver::class.java)
                notifyIntent.putExtra(MainActivity.INTENT_EXTRA_NAVIGATE_TO_DETAILS_ID, reminder)
                val notifyPendingIntent = PendingIntent.getBroadcast(
                        application,
                        reminder._id.toInt(),
                        notifyIntent,
                        0
                )
                val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(notifyPendingIntent)
                repository.deleteVocabularyListStudyReminderByListId(reminder.listId)
            }
        }
    }

    fun updateExpiredWordState() {
        viewModelScope.launch {
            repository.readjustWordStateIntervalsExpiredToday()
        }
    }

    /**
     * Download the latest version of this list from the internet via the correct repository.
     */
    fun updateListById(id: String) {
        _showLoading.value = true
        viewModelScope.launch {
            Log.i("NetworkRequest", "Making a network request")
            repository.downloadVocabularyListById(id)
            _showLoading.value = false
        }
    }

}

//    // The current vocabulary list. Used to populate the recycler view header.
//    private val _currentList = Transformations.switchMap(uuid) {
//        repository.getVocabularyListById(it)
//    }
//    val currentList : LiveData<VocabularyList?>
//        get() = _currentList
//
//    // The words in the list alongside their state - populate the recycler view
//    private val _listWordsWithState = Transformations.switchMap(_uuid) {
//        repository.getUserStateForVocabularyListByListId(it)
//    }
//    val listWordsWithState : LiveData<List<WordWithState>>
//        get() = _listWordsWithState
//
//    // Check whether there is a reminder in the database for the current vocabulary lsit
//    private val _currentStudyReminder = Transformations.switchMap(_uuid) {
//        repository.getStudyReminderByListId(it)
//    }
//    val currentStudyReminder : LiveData<VocabularyListStudyReminder>
//        get() = _currentStudyReminder
//
//    // LiveData to represent the earliest date that the learner could up the level of their words in the list
//    private val _canShowReminderForDate : MutableLiveData<Date?> = MutableLiveData(null)
//    val canShowReminderForDate : LiveData<Date?>
//        get() = _canShowReminderForDate

//    /**
//     * Set the earliest date at which the learner could progress a word up a level. This should be used to decide
//     * the date at which the study reminder should be displayed or the date to show to the learner
//     */
//    fun checkIfCanShowReminder() {
//        viewModelScope.launch {
//            val header = _vocabularyListWithDetails.value?.let {
//                if (it.size > 0) it.get(0) else null
//            }
//            if (header is VocabularyListDetailItem.VocabularyListItem) {
//                val result = repository.getNextReminderDate(header.canShowStudyReminderForDate)
//                _canShowReminderForDate.value = result
//            }
//        }
//    }
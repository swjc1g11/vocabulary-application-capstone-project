package com.swjcook.vocabularyapplication.ui.vocabularypractice

import android.util.Log
import androidx.lifecycle.*
import com.swjcook.vocabularyapplication.domain.WordWithState
import com.swjcook.vocabularyapplication.repositories.VocabularyListRepository
import com.swjcook.vocabularyapplication.ui.vocabularypractice.exercises.Exercise
import com.swjcook.vocabularyapplication.ui.vocabularypractice.exercises.ExerciseGenerator
import com.swjcook.vocabularyapplication.ui.vocabularypractice.exercises.MultipleChoiceExercise
import kotlinx.coroutines.launch
import java.util.*

class VocabularyPracticeViewModel(val repository: VocabularyListRepository) : ViewModel() {

    enum class ShowFeedbackStatus {
        NO_FEEDBACK, SHOW_CORRECT_FEEDBACK, SHOW_INCORRECT_FEEDBACK, PRACTICE_COMPLETE
    }

    private val _uuid = MutableLiveData<String>("")

//    var wordsWithState = Transformations.switchMap(_uuid) {
//        repository.getUserStateForVocabularyListByListId(it, "")
//    }

    var wordsWithState = Transformations.switchMap(_uuid) {
        repository.getWordsWithStateMediated(it, "")
    }

    var exercises : LinkedList<Exercise> = LinkedList()
    var exercisesGenerated = false

    private var _exercisesCompletedCorrectly = 0
    var exercisesCompletedCorrectly = MutableLiveData(0)

    private var _totalNumberOfExercises = 0
    var totalNumberOfExercises = MutableLiveData(0)

    var progress = MutableLiveData(0)

    private var _currentExercise = MutableLiveData<Exercise?>(null)
    val currentExercise : LiveData<Exercise?>
        get() = _currentExercise

    private val _feedbackStatus = MutableLiveData<ShowFeedbackStatus>(ShowFeedbackStatus.NO_FEEDBACK)
    val feedbackStatus: LiveData<ShowFeedbackStatus>
        get() = _feedbackStatus


    fun checkAnswer(exercise: Exercise) {
        if (exercise.isCorrect()) {
            viewModelScope.launch {
                repository.saveWordState(exercise.getWordToUpdate())
            }
            _feedbackStatus.value = ShowFeedbackStatus.SHOW_CORRECT_FEEDBACK
        } else {
            _feedbackStatus.value = ShowFeedbackStatus.SHOW_INCORRECT_FEEDBACK
        }
    }

    fun nextExercise(currentExercise: Exercise) {
        if (currentExercise.isCorrect()) {
            _exercisesCompletedCorrectly += 1
            exercisesCompletedCorrectly.value = _exercisesCompletedCorrectly
            if (_totalNumberOfExercises > 0) {
                progress.value = (_exercisesCompletedCorrectly.toDouble() / _totalNumberOfExercises.toDouble() * 100).toInt()
            }
        } else {
            exercises.addLast(currentExercise)
        }
        if (exercises.isNotEmpty()) {
            _feedbackStatus.value = ShowFeedbackStatus.NO_FEEDBACK
            _currentExercise.postValue(exercises.removeFirst())
        } else {
            _currentExercise.postValue(null)
        }
    }

    fun generateExercises(list: List<WordWithState>) {
        val generator =
            ExerciseGenerator(
                list
            )
        generator.generateMultipleChoiceLanguageTaughtToTranslationLanguage()
        generator.generateMultipleChoiceLanguageTaughtToTranslationLanguage(useHardMode = true)
        exercises = generator.buildQueue()
        exercisesGenerated = true
        _totalNumberOfExercises = exercises.size
        totalNumberOfExercises.value = _totalNumberOfExercises
        _currentExercise.value = exercises.removeFirst()
    }

    fun setListUUID(uuid: String) {
        _uuid.value = uuid
    }

}
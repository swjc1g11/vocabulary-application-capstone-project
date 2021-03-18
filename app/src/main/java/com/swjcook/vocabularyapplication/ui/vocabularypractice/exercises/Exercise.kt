package com.swjcook.vocabularyapplication.ui.vocabularypractice.exercises

import android.util.Log
import com.swjcook.vocabularyapplication.domain.Word
import com.swjcook.vocabularyapplication.domain.WordWithState
import java.util.*
import kotlin.random.Random

interface Exercise {
    fun isCorrect() : Boolean
    fun getWordToUpdate(): WordWithState
}

data class MultipleChoiceExercise(
    val answerOne: WordWithState,
    val answerTwo: WordWithState,
    val correctAnswer: WordWithState,
    val hardMode: Boolean = false
) : Exercise {
    companion object {
        const val EXERCISE_TYPE = "multiple_choice"
    }

    var selectedAnswer: WordWithState? = null

    override fun isCorrect(): Boolean {
        if (selectedAnswer != null) {
            if (selectedAnswer == correctAnswer) return true
        }
        return false
    }

    override fun getWordToUpdate(): WordWithState = correctAnswer
}

class ExerciseGenerator(val wordsWithState: List<WordWithState>) {

    val exercises = mutableListOf<Exercise>()

    fun generateMultipleChoiceLanguageTaughtToTranslationLanguage(useHardMode: Boolean = false) {
        val tempList = wordsWithState.shuffled()
        if (tempList.size >= 2) {
            var i = 0;

            while (i < tempList.size) {
                var randomOptionIndex = getRandomOptionIndex(i, tempList)

                val randomChoice = Random.nextInt(0, 2)
                val x = if (randomChoice == 0) i else randomOptionIndex
                val y = if (x == i) randomOptionIndex else i
                val question =
                    MultipleChoiceExercise(
                        answerOne = tempList.get(x),
                        answerTwo = tempList.get(y),
                        correctAnswer = tempList.get(i),
                        hardMode = useHardMode
                    )
                exercises.add(question)
                i+=1
            }
        }
    }

    fun getRandomOptionIndex(indexUsed: Int, list: List<WordWithState>): Int {
        if (list.size <= 1) return -1
        var n = -1
        while (n == -1) {
            val temp = Random.nextInt(0, list.size)
            if (temp != indexUsed) {
                n = temp
            }
        }
        return n
    }

    fun buildQueue() : LinkedList<Exercise> {
        return LinkedList(exercises)
    }
}
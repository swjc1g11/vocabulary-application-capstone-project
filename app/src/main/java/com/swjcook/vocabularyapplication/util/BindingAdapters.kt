package com.swjcook.vocabularyapplication.util

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.swjcook.vocabularyapplication.R
import com.swjcook.vocabularyapplication.data.entities.UserWordStateLevel
import com.swjcook.vocabularyapplication.domain.VocabularyList
import com.swjcook.vocabularyapplication.domain.VocabularyListStudyReminder
import com.swjcook.vocabularyapplication.domain.WordWithState
import java.util.*
import java.util.concurrent.TimeUnit

@BindingAdapter("networkImage")
fun bindNetworkImage(imageView: ImageView, networkUrl: String?) {
    if (networkUrl != null) {

        if (networkUrl.length > 0) {
            Picasso.with(imageView.context)
                    .load(networkUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView)
        }
    }
}

@BindingAdapter(value=["bind:countryFlagVector"])
fun bindCountryFlagVector(imageView: ImageView, list: VocabularyList) {
    if (list != null) {
        val lang = list.languageTaught
        when (lang) {
            "de-DE" -> {
                imageView.setImageResource(R.drawable.ic_germany)
            }
        }
    }
}

@BindingAdapter(value=["bind:vocabularyListDetailMotivationalText", "bind:vocabularyListComplete"], requireAll = true)
fun bindMotivationalText(textView: TextView, canShowStudyReminderForDate: Date?, listComplete: Boolean = false) {
    val context = textView.context
    if (canShowStudyReminderForDate != null) {
        if (canShowStudyReminderForDate.time < Calendar.getInstance().timeInMillis) {
            textView.text = context.getText(R.string.fragment_vocabulary_list_motivational_text)
        } else {
            val dateFormat = DateFormat.getDateFormat(context)
            textView.text = String.format(
                    context.getString(R.string.fragment_vocabulary_list_motivational_text_with_date),
                    dateFormat.format(canShowStudyReminderForDate)
            )
        }
    } else {
        if (listComplete) {
            textView.text = context.getText(R.string.fragment_vocabulary_list_well_done)
        } else {
            textView.text = context.getText(R.string.fragment_vocabulary_list_motivational_text)
        }
    }
}

@BindingAdapter(value = ["bind:currentStudyReminder", "bind:canShowStudyReminderForDate"], requireAll = true)
fun bindAddEditStudyReminderText(textView: TextView, currentStudyReminder: VocabularyListStudyReminder?, canShowStudyReminderForDate: Date?) {
    // "@{currentStudyReminder != null ? @string/fragment_vocabulary_list_delete_reminder : @string/fragment_vocabulary_list_add_reminder}"
    val context = textView.context
    if (currentStudyReminder != null) {
        textView.isEnabled = true
        textView.text = context.getString(R.string.fragment_vocabulary_list_delete_reminder)
    } else if (canShowStudyReminderForDate != null ){
        val timeDifference = canShowStudyReminderForDate.time - Calendar.getInstance().timeInMillis
        if (timeDifference > 0) {
            textView.isEnabled = true
        } else {
            textView.isEnabled = false
        }
        textView.text = context.getString(R.string.fragment_vocabulary_list_add_reminder)
    } else {
        textView.text = context.getString(R.string.fragment_vocabulary_list_add_reminder)
        textView.isEnabled = false
    }

    // android:enabled="@{canShowStudyReminderForDate != null || currentStudyReminder != null ? true : false}"
}

@BindingAdapter(value=["bind:userWordStateLevelString"])
fun bindUserWordStateLevelAsString(textView: TextView, wordWithState: WordWithState?) {
    if (wordWithState != null) {
        if (wordWithState.state != null) {
            var text = ""
            val context = textView.context
            when (wordWithState!!.state!!.practiceInterval) {
                UserWordStateLevel.LEVEL_ONE -> {
                    text = context.getString(R.string.strength_level_one)
                }
                UserWordStateLevel.LEVEL_TWO -> {
                    text = context.getString(R.string.strength_level_two)
                }
                UserWordStateLevel.LEVEL_THREE -> {
                    text = context.getString(R.string.strength_level_three)
                }
                UserWordStateLevel.LEVEL_FOUR -> {
                    text = context.getString(R.string.strength_level_four)
                }
                UserWordStateLevel.LEVEL_FIVE -> {
                    text = context.getString(R.string.strength_level_five)
                }
            }
            textView.setText(text)
        }
    }
}

@BindingAdapter(value= ["bind:nextPracticeInText"])
fun bingNextPracticeInText(textView: TextView, wordWithState: WordWithState?) {
    if (wordWithState != null) {
        if (wordWithState.state != null) {
            if (wordWithState!!.state!!.wordAcquired == true) {
                textView.text = textView.context.getString(R.string.fragment_vocabulary_list_details_you_have_learned_this_word)
            } else if (wordWithState!!.state!!.nextChangeInIntervalPossibleOn != null) {
                val timeDifference = wordWithState!!.state!!.nextChangeInIntervalPossibleOn!!.time - Calendar.getInstance().timeInMillis
                if (timeDifference < 0)  {
                    textView.text = textView.context.getString(R.string.fragment_vocabulary_list_details_time_to_review)
                } else {
                    val dateFormat = DateFormat.getMediumDateFormat(textView.context)
                    textView.text = String.format(
                            textView.context.getString(R.string.fragment_vocabulary_list_details_practice_next_on),
                            dateFormat.format(wordWithState!!.state!!.nextChangeInIntervalPossibleOn)
                    )
                }
            }
        }
    }
}

@BindingAdapter(value=["bind:layoutMarginBottom"])
fun setLayoutMarginBottom(view: View, dimen: Float) {
    val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.bottomMargin = dimen.toInt()
    view.layoutParams = layoutParams
}

//fun getReviewInText(context: Context, timeDifferenceInMillis: Long): String {
//    val days = TimeUnit.MILLISECONDS.toDays(timeDifferenceInMillis)
//    var text = ""
//
//    if (days == 0L) {
//        val hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceInMillis)
//        if (hours == 0L) {
//            text = context.getString(R.string.fragment_vocabulary_list_details_time_to_review)
//        } else {
//            if (hours == 1L) {
//                text = context.getString(R.string.review_in_one_hour)
//            } else {
//                text = String.format(
//                        context.getString(R.string.review_in_hours),
//                        hours.toString()
//                )
//            }
//        }
//    } else {
//        if (days > 7) {
//            val weeks = days / 7
//            if (weeks == 1L) {
//                text = context.getString(R.string.review_in_one_week)
//            } else {
//                text = String.format(
//                        context.getString(R.string.review_in_weeks),
//                        weeks.toString()
//                )
//            }
//        } else {
//            if (days == 1L) {
//                text =  context.getString(R.string.review_tomorrow)
//            } else {
//                text = String.format(
//                        context.getString(R.string.review_in_days),
//                        days.toString()
//                )
//            }
//        }
//    }
//
//    return text
//}
package com.swjcook.vocabularyapplication.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.swjcook.vocabularyapplication.MainActivity
import com.swjcook.vocabularyapplication.domain.VocabularyListStudyReminder
import com.swjcook.vocabularyapplication.util.sendNotification
import java.util.*

class VocabularyListStudyReminderReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
        ) as NotificationManager

        val extras = intent.extras
        extras?.let {
            if (it.containsKey(MainActivity.INTENT_EXTRA_NAVIGATE_TO_DETAILS_ID)) {
                val bundle = it.getBundle(MainActivity.INTENT_EXTRA_NAVIGATE_TO_DETAILS_ID)
                bundle?.let { innerBundle ->
                    val reminder = innerBundle.getParcelable<VocabularyListStudyReminder>(MainActivity.INTENT_EXTRA_NAVIGATE_TO_DETAILS_ID)
                    reminder?.let { r ->
                        notificationManager.sendNotification(context, r)
                    }
                }
            }
        }
    }
}
package com.swjcook.vocabularyapplication.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.swjcook.vocabularyapplication.BuildConfig
import com.swjcook.vocabularyapplication.MainActivity
import com.swjcook.vocabularyapplication.R
import com.swjcook.vocabularyapplication.domain.VocabularyListStudyReminder

fun NotificationManager.sendNotification(context: Context, vocabularyListStudyReminder: VocabularyListStudyReminder) {
    val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(MainActivity.INTENT_EXTRA_NAVIGATE_TO_DETAILS_ID, vocabularyListStudyReminder)

    val pendingIntent = PendingIntent.getActivity(
            context,
            vocabularyListStudyReminder.listId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notificationBuilder = NotificationCompat.Builder(context, context.resources.getString(R.string.reminder_notification_channel_id))
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(context.getString(R.string.notification_title_study_reminder))
            .setContentText(context.getString(R.string.notification_description_study_reminder))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

    notify(vocabularyListStudyReminder.listId.hashCode(), notificationBuilder.build())
}
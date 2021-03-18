package com.swjcook.vocabularyapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.swjcook.vocabularyapplication.domain.VocabularyListStudyReminder
import com.swjcook.vocabularyapplication.ui.vocabularylistdetail.VocabularyListDetailFragmentArgs


class MainActivity : AppCompatActivity() {

    companion object {
        const val INTENT_EXTRA_NAVIGATE_TO_DETAILS_ID = "intent_extra_to_details_id"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_about), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        createNotificationChannel()

        val listId = intent.getParcelableExtra<VocabularyListStudyReminder>(MainActivity.INTENT_EXTRA_NAVIGATE_TO_DETAILS_ID)
        if (listId != null) {
            val args =  VocabularyListDetailFragmentArgs(listUUID = listId.listId)
            findNavController(R.id.nav_host_fragment).navigate(
                    R.id.vocabularyListDetailFragment,
                    args.toBundle()
            )
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun createNotificationChannel() {
        val notificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = getString(R.string.reminder_notification_channel_id)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && notificationManager.getNotificationChannel(channelId) == null
        ) {
            val name = applicationContext.getString(R.string.app_name)
            val channel = NotificationChannel(
                    channelId,
                    name,
                    NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableLights(true)
            channel.lightColor = Color.GREEN
            notificationManager.createNotificationChannel(channel)
        }
    }
}
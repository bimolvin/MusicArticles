package com.example.musicarticles.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.musicarticles.R
import com.example.musicarticles.data.User
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.concurrent.fixedRateTimer

class NotificationService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        fixedRateTimer("timer", false, 10000.toLong(), 3600 * 1000) {
            User.userLastUpdate?.let {
                val lastAdd = it.toInstant()
                val now = Calendar.getInstance().time.toInstant()
                if(ChronoUnit.HOURS.between(lastAdd, now) >= 24) {
                    setAlarm()
                }
            }
        }
    }

    private fun setAlarm() {
        val alarmIntent = Intent(this, NotificationReceiver::class.java)

        alarmIntent.putExtra(
            "Notification title",
            resources.getString(R.string.notification_title)
        )
        alarmIntent.putExtra(
            "Notification description",
            resources.getString(R.string.notification_description)
        )

        val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
        )

        val manager = getSystemService(ALARM_SERVICE) as AlarmManager
        manager[AlarmManager.RTC, System.currentTimeMillis()] = pendingIntent
    }
}
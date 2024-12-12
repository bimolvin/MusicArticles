package com.example.musicarticles

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.musicarticles.articleList.ArticleListFragment
import com.example.musicarticles.data.articleList
import com.example.musicarticles.editor.EditorFragment
import com.example.musicarticles.json.pushArticleListToFile
import com.example.musicarticles.notifications.CHANNEL_ID
import com.example.musicarticles.notifications.NotificationService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File


const val FILE_NAME = "articles.json"

class MainActivity : AppCompatActivity() {
    private val fragmentList = listOf(
        ArticleListFragment.newInstance(),
        EditorFragment.newInstance()
    )

    private val icons = listOf (
        R.drawable.home,
        R.drawable.edit
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
        if (existingChannel == null) {
            createNotificationChannel()
        }

//        startService(Intent(this, NotificationService::class.java))

//        createInitialArticles()

        val adapter = FragmentAdapter(this, fragmentList)
        val viewPager: ViewPager2 = findViewById(R.id.pager)
        viewPager.adapter = adapter

        val tabLayout: TabLayout = findViewById(R.id.bottom_navigation)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(icons[position])
        }.attach()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun createNotificationChannel() {
        val name = "New article notification"
        val descriptionText = "This notification motivates you to write more"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun createInitialArticles() {
        pushArticleListToFile(File(filesDir, FILE_NAME), articleList(resources))
    }

    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1
    }
}

package com.devanasmohammed.fwd_load_app

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.devanasmohammed.fwd_load_app.utils.createNotificationChannel
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private var selectedUrl = ""
    private var downloadFileName = ""
    private var downloadID: Long = 0
    private lateinit var loadingButton: LoadingButton
    private lateinit var notificationManager: NotificationManager
    private var downloadStatus = "Failed"

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton = findViewById(R.id.custom_button)
        custom_button.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            loadingButton.buttonState = ButtonState.Completed

            val action = intent?.action
            if (downloadID == id) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(id)
                    val downloadManager =
                        context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        when(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))){
                            DownloadManager.STATUS_SUCCESSFUL->{
                                downloadStatus = "Success"
                                sendNotification(downloadFileName, downloadStatus)
                            }
                            else->{
                                downloadStatus = "Failed"
                                sendNotification(downloadFileName, downloadStatus)
                            }
                        }
                    }
                    cursor.close()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun download() {
        loadingButton.buttonState = ButtonState.Clicked

        if (selectedUrl.isNotEmpty()) {
            loadingButton.buttonState = ButtonState.Loading
            notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
            createNotificationChannel(
                this,
                CHANNEL_ID,
                "Downloading Files",
                "Downloading files from different sources",
                NotificationManager.IMPORTANCE_HIGH
            )

            val file = File(getExternalFilesDir(null), "/repositories")
            if (!file.exists()) {
                file.mkdirs()
            }

            val request =
                DownloadManager.Request(Uri.parse(selectedUrl))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            loadingButton.buttonState = ButtonState.Completed
            Toast.makeText(this, "Please select file to download", Toast.LENGTH_SHORT).show()
        }
    }

    fun onRadioButtonSelected(view: View) {
        if (view is RadioButton && view.isChecked) {
            when (view.id) {
                R.id.glide_radio_btn -> {
                    selectedUrl = GLIDE_URL
                    downloadFileName = "Glide"
                }
                R.id.load_app_radio_btn -> {
                    selectedUrl = LOAD_APP_URL
                    downloadFileName = "LoadApp"
                }
                R.id.retrofit_radio_btn -> {
                    selectedUrl = RETROFIT_URL
                    downloadFileName = "Retrofit"
                }
            }
        }
    }

    companion object {
        //Repo Links
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"

        //Notification
        private const val CHANNEL_ID = "channelId"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun sendNotification(downloadFileName: String, downloadStatus: String) {
        val detailIntent = Intent(this, DetailActivity::class.java)
        detailIntent.putExtra("fileName", downloadFileName)
        detailIntent.putExtra("status", downloadStatus)

        val activityPendingIntent = PendingIntent
            .getActivity(
                this,
                10,
                detailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("The $downloadFileName has been downloaded")
            .setContentText("Click to see the status of the download")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(activityPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Check the status",
                activityPendingIntent
            )
            .build()

        notificationManager.notify(
            10, notification
        )
    }
}

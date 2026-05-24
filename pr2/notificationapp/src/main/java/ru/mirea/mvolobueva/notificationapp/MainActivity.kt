package ru.mirea.mvolobueva.notificationapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "com.mirea.asd.notification.ANDROID"
    private val PermissionCode = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationApp", "Разрешения получены")
            } else {
                Log.d("NotificationApp", "Нет разрешений! Запрашиваем...")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), PermissionCode)
            }
        }

        val btnSend = findViewById<Button>(R.id.btnSendNotification)

        btnSend.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return@setOnClickListener
            }

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Mirea")
                .setContentText("Congratulation!")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle().bigText("Much longer text that cannot fit one line..."))

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "Volobueva M.A. Notification", importance)
            channel.description = "MIREA Channel"

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.createNotificationChannel(channel)

            notificationManager.notify(1, builder.build())
        }
    }
}
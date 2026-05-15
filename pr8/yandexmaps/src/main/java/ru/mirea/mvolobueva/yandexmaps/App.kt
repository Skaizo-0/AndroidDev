package ru.mirea.mvolobueva.yandexmaps

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey("32ec98a3-77cb-47ea-b951-735fe29bf78b")
    }
}
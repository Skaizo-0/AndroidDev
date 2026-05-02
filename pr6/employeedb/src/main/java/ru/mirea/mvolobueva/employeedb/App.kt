package ru.mirea.mvolobueva.employeedb

import android.app.Application
import androidx.room.Room

class App : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "superheroes_database"
        )
            .allowMainThreadQueries()
            .build()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
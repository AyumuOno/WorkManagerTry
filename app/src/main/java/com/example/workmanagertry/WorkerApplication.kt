package com.example.workmanagertry

import android.app.Application
import timber.log.Timber

class WorkerApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}

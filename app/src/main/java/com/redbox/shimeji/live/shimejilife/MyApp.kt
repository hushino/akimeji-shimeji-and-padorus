package com.redbox.shimeji.live.shimejilife

import android.app.Application
import timber.log.Timber
import com.redbox.shimeji.live.shimejilife.di.ServiceLocator

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
            Timber.plant(Timber.DebugTree())
        ServiceLocator.init(this)
    }
}
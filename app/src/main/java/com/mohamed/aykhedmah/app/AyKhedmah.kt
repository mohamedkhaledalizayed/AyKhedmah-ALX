package com.mohamed.aykhedmah.app

import android.content.Context
import androidx.multidex.MultiDex
import com.mohamed.aykhedmah.di.AppComponent
import com.mohamed.aykhedmah.di.DaggerAppComponent
import dagger.android.DaggerApplication

class AyKhedmah: DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

    }
    override fun applicationInjector(): AppComponent {
        return DaggerAppComponent.builder().application(this).build()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}
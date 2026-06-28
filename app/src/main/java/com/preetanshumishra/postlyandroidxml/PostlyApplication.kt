package com.preetanshumishra.postlyandroidxml

import android.app.Application
import com.preetanshumishra.postlyandroidxml.di.AppComponent
import com.preetanshumishra.postlyandroidxml.di.DaggerAppComponent

class PostlyApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.create()
    }
}

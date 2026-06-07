package com.preetanshu.postlyandroidxml

import android.app.Application
import com.preetanshu.postlyandroidxml.di.AppComponent
import com.preetanshu.postlyandroidxml.di.DaggerAppComponent

class PostlyApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.create()
    }
}

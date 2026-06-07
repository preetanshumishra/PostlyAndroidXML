package com.preetanshu.postlyandroidxml.di

import com.preetanshu.postlyandroidxml.screens.login.LoginActivity
import com.preetanshu.postlyandroidxml.screens.postlist.PostListActivity
import com.preetanshu.postlyandroidxml.services.ImageLoaderService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: LoginActivity)
    fun inject(activity: PostListActivity)
    fun imageLoaderService(): ImageLoaderService
}

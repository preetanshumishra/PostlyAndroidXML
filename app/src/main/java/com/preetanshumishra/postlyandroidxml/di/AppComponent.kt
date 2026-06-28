package com.preetanshumishra.postlyandroidxml.di

import com.preetanshumishra.postlyandroidxml.screens.login.LoginActivity
import com.preetanshumishra.postlyandroidxml.screens.postlist.PostListActivity
import com.preetanshumishra.postlyandroidxml.services.ImageLoaderService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: LoginActivity)
    fun inject(activity: PostListActivity)
    fun imageLoaderService(): ImageLoaderService
}

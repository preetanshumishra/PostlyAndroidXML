package com.preetanshu.postlyandroidxml.di

import com.preetanshu.postlyandroidxml.services.ImageLoaderService
import com.preetanshu.postlyandroidxml.services.ImageLoaderServiceImplementation
import com.preetanshu.postlyandroidxml.services.NetworkService
import com.preetanshu.postlyandroidxml.services.NetworkServiceImplementation
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideNetworkService(): NetworkService = NetworkServiceImplementation()

    @Provides
    @Singleton
    fun provideImageLoaderService(): ImageLoaderService = ImageLoaderServiceImplementation()
}

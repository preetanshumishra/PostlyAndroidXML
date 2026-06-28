package com.preetanshumishra.postlyandroidxml.di

import com.preetanshumishra.postlyandroidxml.services.ImageLoaderService
import com.preetanshumishra.postlyandroidxml.services.ImageLoaderServiceImplementation
import com.preetanshumishra.postlyandroidxml.services.NetworkService
import com.preetanshumishra.postlyandroidxml.services.NetworkServiceImplementation
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

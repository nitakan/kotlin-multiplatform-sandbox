package com.jetbrains.kmpapp

import android.app.Application
import com.jetbrains.kmpapp.data.client.PlatformRepositoryFactory
import com.jetbrains.kmpapp.di.PlatformRepositoryFactoryImpl
import com.jetbrains.kmpapp.di.initKoin
import com.jetbrains.kmpapp.domain.repository.PlatformRepository
import com.jetbrains.kmpapp.repository.PlatformRepositoryImpl
import com.jetbrains.kmpapp.screens.DisplayDataViewModel
import org.koin.dsl.module

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            listOf(
                module {
                    factory<PlatformRepository> { PlatformRepositoryImpl() }
                    factory<PlatformRepositoryFactory> { PlatformRepositoryFactoryImpl(get()) }
                    factory { DisplayDataViewModel(get(), get()) }
                }
            )
        )
    }
}

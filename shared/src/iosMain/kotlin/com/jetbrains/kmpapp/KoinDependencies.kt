package com.jetbrains.kmpapp

import com.jetbrains.kmpapp.data.client.PlatformRepositoryFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KoinDependencies : KoinComponent {
    val platformRepositoryFactory: PlatformRepositoryFactory by inject()
}

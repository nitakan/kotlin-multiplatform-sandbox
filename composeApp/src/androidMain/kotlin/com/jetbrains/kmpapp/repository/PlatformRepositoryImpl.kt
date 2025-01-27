package com.jetbrains.kmpapp.repository

import com.jetbrains.kmpapp.domain.entities.PlatformData
import com.jetbrains.kmpapp.domain.repository.PlatformRepository
import kotlinx.coroutines.delay

class PlatformRepositoryImpl: PlatformRepository {
    override fun getDataSync(): PlatformData {
        return PlatformData("[Android]The client generated sync data")
    }

    override suspend fun getDataAsync(): PlatformData {
        delay(1000L)
        return PlatformData("[Android]The client generated async data")
    }
}
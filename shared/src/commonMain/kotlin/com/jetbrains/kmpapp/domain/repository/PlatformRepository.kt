package com.jetbrains.kmpapp.domain.repository

import com.jetbrains.kmpapp.domain.entities.PlatformData

interface PlatformRepository {
    fun getDataSync(): PlatformData
    suspend fun getDataAsync(): PlatformData
}
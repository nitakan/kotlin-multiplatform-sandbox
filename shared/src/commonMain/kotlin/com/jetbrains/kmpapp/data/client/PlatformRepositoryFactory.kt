package com.jetbrains.kmpapp.data.client

import com.jetbrains.kmpapp.domain.repository.PlatformRepository

interface PlatformRepositoryFactory {
    fun createPlatformRepository(): PlatformRepository
}
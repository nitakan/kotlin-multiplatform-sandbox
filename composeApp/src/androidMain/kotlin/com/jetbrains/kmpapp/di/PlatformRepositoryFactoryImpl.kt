package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.data.client.PlatformRepositoryFactory
import com.jetbrains.kmpapp.domain.repository.PlatformRepository

class PlatformRepositoryFactoryImpl(
    private val platformRepository: PlatformRepository,
) : PlatformRepositoryFactory {
    override fun createPlatformRepository() = platformRepository
}
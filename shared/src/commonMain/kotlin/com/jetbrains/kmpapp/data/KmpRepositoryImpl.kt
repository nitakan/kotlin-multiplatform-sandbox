package com.jetbrains.kmpapp.data

import com.jetbrains.kmpapp.domain.entities.KmpData
import com.jetbrains.kmpapp.domain.repository.KmpRepository

class KmpRepositoryImpl: KmpRepository {
    override suspend fun getKmpData(): KmpData {
        return KmpData("KMP data: ${uuid()}")
    }
}
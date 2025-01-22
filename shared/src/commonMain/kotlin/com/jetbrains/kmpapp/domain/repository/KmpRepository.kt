package com.jetbrains.kmpapp.domain.repository

import com.jetbrains.kmpapp.domain.entities.KmpData

interface KmpRepository {
    suspend fun getKmpData(): KmpData
}
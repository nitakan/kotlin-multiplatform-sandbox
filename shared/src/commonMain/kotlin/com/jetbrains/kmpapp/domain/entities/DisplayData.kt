package com.jetbrains.kmpapp.domain.entities

sealed class DisplayData {

    data class Loaded(
        val clientValue: PlatformData,
        val kmpValue: KmpData,
    ) : DisplayData()

    data object Loading : DisplayData()

    companion object {
        fun loading() = Loading
    }
}


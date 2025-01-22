package com.jetbrains.kmpapp.screens

import com.jetbrains.kmpapp.domain.entities.DisplayData
import com.jetbrains.kmpapp.domain.repository.KmpRepository
import com.jetbrains.kmpapp.domain.repository.PlatformRepository
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DisplayDataViewModel(
    private val kmpRepository: KmpRepository,
    private val platformRepository: PlatformRepository,
) : ViewModel() {

    private val _displayData = MutableStateFlow<DisplayData>(DisplayData.loading())

    @NativeCoroutinesState
    val displayData: StateFlow<DisplayData> = _displayData.asStateFlow()

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    suspend fun loadData() {
        _displayData.emit(DisplayData.Loading)
        delay(1000)
        val platformSyncData = platformRepository.getDataSync()
        val kmp = kmpRepository.getKmpData()
        _displayData.emit(DisplayData.Loaded(platformSyncData, kmp))
        val platformAsyncData = platformRepository.getDataAsync()
        _displayData.emit(DisplayData.Loaded(platformAsyncData, kmp))
    }
}

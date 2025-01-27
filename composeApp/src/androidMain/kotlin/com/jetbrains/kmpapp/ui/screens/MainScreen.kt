package com.jetbrains.kmpapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.kmpapp.domain.entities.DisplayData
import com.jetbrains.kmpapp.screens.DisplayDataViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen() {
    val viewModel: DisplayDataViewModel = koinViewModel()
    val objects by viewModel.displayData.collectAsStateWithLifecycle(DisplayData.loading())
    val coroutine = rememberCoroutineScope()

    Content(
        item = objects,
        onLoad = {
            coroutine.launch {
                viewModel.loadData()
            }
        },
    )
}

@Composable
private fun Content(
    item: DisplayData,
    onLoad: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        when (item) {
            DisplayData.Loading -> Text("Loading...")
            is DisplayData.Loaded -> {
                Text("Loaded")
                Text("ClientValue: ${item.clientValue.value}")
                Text("KmpValue: ${item.kmpValue.value}")
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        ElevatedButton(
            onClick = onLoad,
        ) {
            Text("Load data")
        }
    }
}

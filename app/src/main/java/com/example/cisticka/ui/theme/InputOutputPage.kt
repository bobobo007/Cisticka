package com.example.cisticka.ui.theme

import androidx.compose.material.icons.Icons
import com.example.cisticka.R
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cisticka.ui.theme.ApiService.WebSocketData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputOutputPage(
    navigateBack: () -> Unit,
    webSocketData: WebSocketData,
    isWebSocketConnected: Boolean
) {
    val orientation = LocalConfiguration.current.orientation
    var inputs by remember { mutableStateOf(webSocketData.ip) }
    var outputs by remember { mutableStateOf(webSocketData.ou) }

    // Funkcia na odoslanie správy na WebSocket server
    fun sendWebSocketMessage(command: String, state: Boolean) {
        val message = "{\"com\":\"$command\",\"sta\":$state}"
        ApiService.sendMessage(message)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.inputs_outputs),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isWebSocketConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = if (isWebSocketConnected) stringResource(R.string.online) else stringResource(R.string.offline),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isWebSocketConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(Icons.Default.Home, contentDescription = stringResource(R.string.home))
                    }
                },
                modifier = Modifier
                    .width(600.dp)
                    .padding(horizontal = if (orientation == Configuration.ORIENTATION_PORTRAIT) 8.dp else 25.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .width(600.dp)
                .padding(innerPadding)
                .padding(horizontal = if (orientation == Configuration.ORIENTATION_PORTRAIT) 8.dp else 25.dp)
                .padding(top = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Input Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.inputs_title),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    listOf(
                        stringResource(R.string.dripping_valve),
                        stringResource(R.string.cleaning_valve),
                        stringResource(R.string.tank),
                        stringResource(R.string.input4)
                    ).forEachIndexed { index, line ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (inputs[index]) {
                                DarkLed {}
                            } else {
                                BrightLed {}
                            }
                        }
                    }
                }
            }

            // Output Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.outputs_title),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    listOf(
                        stringResource(R.string.dripping_valve),
                        stringResource(R.string.cleaning_valve),
                        stringResource(R.string.heating),
                        stringResource(R.string.output4)
                    ).forEachIndexed { index, line ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (outputs[index]) {
                                DarkLed {
                                    val newState = !outputs[index]
                                    outputs = outputs.toMutableList().also { it[index] = newState }
                                    sendWebSocketMessage("ou${index + 1}", newState)
                                }
                            } else {
                                BrightLed {
                                    val newState = !outputs[index]
                                    outputs = outputs.toMutableList().also { it[index] = newState }
                                    sendWebSocketMessage("ou${index + 1}", newState)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewInputOutputPage() {
    CistickaTheme {
        InputOutputPage(navigateBack = {},
            webSocketData = WebSocketData(),
            isWebSocketConnected = true)
    }
}
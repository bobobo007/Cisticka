package com.example.cisticka

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.cisticka.ui.theme.CistickaTheme
import com.example.cisticka.ui.theme.DarkLed
import com.example.cisticka.ui.theme.BrightLed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.cisticka.ui.theme.ApiService
import com.example.cisticka.ui.theme.ApiService.WebSocketData
import com.example.cisticka.ui.theme.InfoPage
import com.example.cisticka.ui.theme.SetupPage
import com.example.cisticka.ui.theme.LogPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.String
import kotlin.collections.List

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val sharedPrefs = newBase.getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val languageCode = sharedPrefs.getString("language", "sk") ?: "sk"
        super.attachBaseContext(applyLocale(newBase, languageCode))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiService.loadWebSocketUrl(this) // <<--- DOPLNENÉ sem
        enableEdgeToEdge()
        setContent {
            CistickaTheme {
                var currentPage by rememberSaveable { mutableStateOf("MainPage") }
                val isWebSocketConnected by ApiService.isWebSocketConnected.collectAsState(false)
                val webSocketData by ApiService.webSocketData.collectAsState()
                val logsFlow = ApiService.logsf

                val translateError: (String) -> String = { message ->
                    getTranslatedErrorMessage(this, message)
                }

                when (currentPage) {
                    "MainPage" -> MainPageWithMenuAndCards(
                        navigateToInfo = { currentPage = "InfoPage" },
                        navigateToSetup = { currentPage = "SetupPage" },
                        navigateToLogs = { currentPage = "LogPage" },
                        webSocketData = webSocketData,
                        isWebSocketConnected = isWebSocketConnected
                    )
                    "InfoPage" -> InfoPage(
                        navigateBack = { currentPage = "MainPage" },
                        webSocketData = webSocketData,
                        isWebSocketConnected = isWebSocketConnected
                    )
                    "SetupPage" -> SetupPage(
                        navigateBack = { currentPage = "MainPage" },
                        isWebSocketConnected = isWebSocketConnected,
                        getTranslatedErrorMessage = translateError
                    )
                    "LogPage" -> LogPage(
                        navigateBack = { currentPage = "MainPage" },
                        logsFlow = logsFlow,
                        isWebSocketConnected = isWebSocketConnected,
                        getTranslatedErrorMessage = translateError
                    )
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        ApiService.connectWebSocket(
            onClosed = { reason ->
                Log.d("WebSocket", "Closed: $reason")
            },
            onFailure = { error ->
                Log.e("WebSocket", "Error: ${error.message}")
            }
        )
    }
    override fun onStop() {
        super.onStop()
        ApiService.closeWebSocket()
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        ApiService.closeWebSocket()
    }
}

fun getTranslatedErrorMessage(context: Context, message: String): String {
    val resId = errorTranslations[message]
    return if (resId != null) {
        context.getString(resId)
    } else {
        message // Ak preklad neexistuje, použije sa pôvodná správa
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPageWithMenuAndCards(navigateToInfo: () -> Unit, navigateToSetup: () -> Unit, navigateToLogs: () -> Unit, webSocketData: WebSocketData, isWebSocketConnected: Boolean) {
    val orientation = LocalConfiguration.current.orientation
    var expanded by remember { mutableStateOf(false)
    }
    val errorMessage by ApiService.errorMessage.collectAsState() // Sledovanie chýb
    val coroutineScope = rememberCoroutineScope()




    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.main_page), modifier = Modifier.weight(1f))
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
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.information)) },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                            onClick = {
                                expanded = false
                                navigateToInfo()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.settings)) },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            onClick = {
                                expanded = false
                                navigateToSetup()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.log_file)) },
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                            onClick = {
                                expanded = false
                                navigateToLogs()
                            }
                        )
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
                .heightIn(min = 400.dp, max = 900.dp) // Obmedzenie výšky
                .padding(innerPadding)
                .padding(horizontal = if (orientation == Configuration.ORIENTATION_PORTRAIT) 8.dp else 25.dp)
                .padding(top = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Zobrazenie chybovej hlášky
            errorMessage?.let { message ->
                val context = LocalContext.current
                val translatedMessage = getTranslatedErrorMessage(context, message)



                Text(
                    text = translatedMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
                coroutineScope.launch {
                    delay(10_000) // 30 sekúnd
                    if (ApiService.errorMessage.value == message) {
                        ApiService.clearErrorMessage() // Bezpečná mutácia cez metódu
                    }
                }
            }

            CardWithText(title = stringResource(R.string.values),
                lines = listOf(
                    stringResource(R.string.temperature) to "${webSocketData.te} °C",
                    stringResource(R.string.depth) to "${webSocketData.de} mm",
                    stringResource(R.string.wifi) to "${webSocketData.wi} dB"
                ),
                states = webSocketData.cl,
                flag = webSocketData.fl,
                tank = webSocketData.ip[2]
            )
            CardWithText(
                title = stringResource(R.string.control),
                lines = listOf(
                    stringResource(R.string.dripping) to "",
                    stringResource(R.string.cleaning) to "",
                ),
                showButtons = true,
                states = webSocketData.cl,
                flag = webSocketData.fl,
                tank = webSocketData.ip[2]
            )
            CardWithDynamicLeds(
                title = stringResource(R.string.inputs_title),
                lines = listOf(
                    stringResource(R.string.dripping_valve),
                    stringResource(R.string.cleaning_valve),
                    stringResource(R.string.tank),
                    stringResource(R.string.input4)
                ),
                states = webSocketData.ip,
                outputInfo = false
            )
            CardWithDynamicLeds(
                title = stringResource(R.string.outputs_title),
                lines = listOf(
                    stringResource(R.string.dripping_valve),
                    stringResource(R.string.cleaning_valve),
                    stringResource(R.string.heating),
                    stringResource(R.string.output4)
                ),
                states = webSocketData.ou,
                outputInfo = true
            )
        }
    }
}

@Composable
fun CardWithText(
    title: String,
    lines: List<Pair<String, String>>,
    showButtons: Boolean = false,
    states: Boolean,
    flag: Boolean,
    tank: Boolean
) {
    val context = LocalContext.current
    var webSocketData by remember { mutableStateOf(WebSocketData()) }
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
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (showButtons) {
                // Jeden riadok: Tri Buttony
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // First Button - Dripp
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    ApiService.sendMessage("{\"com\":\"dr\",\"sta\":true}")
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, context.getString(R.string.error) + " ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (flag) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        ),
                        enabled = !flag
                    ) {
                        Text(stringResource(R.string.dripping), style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Second Button - Switch
                    Button(
                        onClick = {
                            val newSwitchState = !states // Toggle the current state
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    // Send the new state to the WebSocket server
                                    ApiService.sendMessage("{\"com\":\"cl\",\"sta\":$newSwitchState}")
                                    withContext(Dispatchers.Main) {
                                        // Optionally update the local state immediately (if no server response is expected to confirm)
                                        webSocketData = webSocketData.copy(cl = newSwitchState)
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, context.getString(R.string.error) + " ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tank && states) MaterialTheme.colorScheme.inversePrimary else
                                    if (states) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

                        )
                    ) {
                        Text(
                            text = if (states) stringResource(R.string.cleaning) else stringResource(R.string.recirculation),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            } else {
                // Riadky s textom a hodnotami
                lines.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardWithDynamicLeds(
    title: String,
    lines: List<String>,
    states: List<Boolean>,
    outputInfo: Boolean,
) {
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
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            lines.forEachIndexed { index, line ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (outputInfo) {
                        if (states.getOrNull(index) == true) {
                            DarkLed {
                                val newState = !states[index]
                                sendWebSocketMessage("ou${index + 1}", newState)
                            }
                        } else {
                            BrightLed {
                                val newState = !states[index]
                                sendWebSocketMessage("ou${index + 1}", newState)
                            }
                        }
                    } else {
                        if (states.getOrNull(index) == true) {
                            DarkLed {}
                        } else {
                            BrightLed {}
                        }
                    }
                }
            }
        }
    }
}
// Funkcia na odoslanie správy na WebSocket server
fun sendWebSocketMessage(command: String, state: Boolean) {
    val message = "{\"com\":\"$command\",\"sta\":$state}"
    ApiService.sendMessage(message)
}

fun formatLogData(rawData: String): List<Pair<String, String>> {
    val logs = mutableListOf<Pair<String, String>>()

    // Rozdelenie logov na jednotlivé záznamy
    val logEntries = rawData.trim('[', ']').split("\n")

    for (entry in logEntries) {
        val parts = entry.split(",")

        if (parts.size >= 8) {
            // Extrahovanie údajov z logu
            val timestamp = "${parts[1]} ${parts[2]}"
            val temperature = parts[3]
            val depth = parts[4]
            val inputs = parts[5]
            val outputs = parts[6]
            val message = parts.subList(7, parts.size).joinToString(",").trim()

            // Pridanie formátovaných údajov
            val line1 = "$timestamp $temperature°C ${depth}mm $inputs $outputs"
            if (line1.isNotBlank() && message.isNotBlank()) {
                logs.add(Pair(line1, message))
            }
        }
    }

    return logs
}

fun applyLocale(context: Context, languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = context.resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    return context.createConfigurationContext(config)
}
// Mapa kódov na stringRes ID
private val errorTranslations = mapOf(
    "temperatureNotReached" to R.string.temperatureNotReached,
    "temperatureToLow" to R.string.temperatureToLow,
    "couldNotOpenLogFile" to R.string.couldNotOpenLogFile,
    "SDCardError" to R.string.SDCardError,
    "NTPError" to R.string.NTPError,
    "TimeSetError" to R.string.TimeSetError,
    "noResponse" to R.string.noResponse,
)

@Preview(showBackground = true)
@Composable
fun PreviewMainPageWithMenuAndCards() {
    MainPageWithMenuAndCards(
        navigateToInfo = {},
        navigateToSetup = {},
        navigateToLogs = {},
        webSocketData = WebSocketData(),
        isWebSocketConnected = true
    )
}
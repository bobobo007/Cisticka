package com.example.cisticka.ui.theme
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cisticka.R
import com.example.cisticka.formatLogData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogPage(
    navigateBack: () -> Unit,
    logsFlow: StateFlow<List<String>>,
    isWebSocketConnected: Boolean
) {
    val context = LocalContext.current
    val orientation = LocalConfiguration.current.orientation
    val logs by logsFlow.collectAsState()

    fun sendLogFile() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiService.sendMessage("{\"com\":\"sl\",\"sta\":true}")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.error) + " ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                }
            }
        }
    }

    // Funkcia na získanie preloženej správy
    fun getTranslatedLog(line2: String, context: Context): String {
        val resId = logTranslations[line2] ?: R.string.unknowLog // Predvolený text pre neznáme kódy
        return context.getString(resId)
    }

    // Príklad použitia
    fun processLog(line2: String, context: Context): String {
        return getTranslatedLog(line2, context)
    }


    // Spustenie `sendLogFile` pri otvorení stránky
    LaunchedEffect(Unit) {
        sendLogFile()
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
                            text = stringResource(R.string.log_page),
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .width(600.dp)
                .padding(innerPadding)
                .padding(horizontal = if (orientation == Configuration.ORIENTATION_PORTRAIT) 8.dp else 25.dp)
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 700.dp) // Obmedzenie výšky
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = stringResource(R.string.log_file),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    logs.take(50).forEach { log ->
                        val formattedLogs = formatLogData(log)
                        formattedLogs.forEach { (line1, line2) ->
                            Text(
                                text = line1,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                            Text(
                   //             text = line2,
                                text = processLog(line2, context),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Mapa kódov na stringRes ID
private val logTranslations = mapOf(
    "WiFiNotConnected" to R.string.WiFiNotConnected,
    "VCCDisconnected" to R.string.VCCDisconnected,
    "Start" to R.string.Start,
    "WiFicould" to R.string.WiFicould,
    "HeatingON" to R.string.HeatingON,
    "TemperatureToLow" to R.string.TemperatureToLow,
    "VetilInPosition" to R.string.VetilInPosition,
    "VentilNotInPosition" to R.string.VentilNotInPosition,
    "HeatingOFF" to R.string.HeatingOFF,
    "VentilToDrainPos" to R.string.VentilToDrainPos,
    "VentilToCleaning" to R.string.VentilToCleaning,
    "VentilIsInCleaning" to R.string.VentilIsInCleaning,
    "VentilNotInCleaning" to R.string.VentilNotInCleaning,
    "VentilToRecirkulation" to R.string.VentilToRecirkulation,
    "VentilIsInRecirkulacion" to R.string.VentilIsInRecirkulacion,
    "VentilNotInRecirkulacion" to R.string.VentilNotInRecirkulacion,
    "LogFileDeleted" to R.string.LogFileDeleted,
    "FailedToDelete" to R.string.FailedToDelete,
    "WiFiDisconnected" to R.string.WiFiDisconnected,
    "DrainOn" to R.string.DrainOn,
    "SwitchCleaning" to R.string.SwitchCleaning,
    "SwitchRecirkulacion" to R.string.SwitchRecirkulacion,
    "TimeSet" to R.string.TimeSet,
    "NTPSet" to R.string.NTPSet,
    "OtputChanged" to R.string.OtputChanged,
    "InputChanged" to R.string.InputChanged,
)

@Composable
@Preview
fun PreviewLogPage() {
    val fakeLogs = MutableStateFlow(
        List(10) { index -> "Log entry #$index: Sample log message." }
    )

    LogPage(
        navigateBack = {},
        logsFlow = fakeLogs,
        isWebSocketConnected = true
    )
}

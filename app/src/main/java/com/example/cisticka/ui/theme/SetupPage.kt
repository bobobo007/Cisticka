package com.example.cisticka.ui.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cisticka.R
import com.example.cisticka.ui.theme.ApiService.WebSocketData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupPage(navigateBack: () -> Unit, webSocketData: WebSocketData, isWebSocketConnected: Boolean) {
    val context = LocalContext.current
    var isDeleteLoading by remember { mutableStateOf(false) }
    var isButtonNTPLoading by remember { mutableStateOf(false) }
    var isButtonTimeLoading by remember { mutableStateOf(false) }
    var serverAddress by rememberSaveable { mutableStateOf(ApiService.getWebSocketUrl()) }
    var selectedLanguage by rememberSaveable { mutableStateOf("sk") }
    val sharedPrefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    var currentLanguage by remember { mutableStateOf(sharedPrefs.getString("language", "sk") ?: "sk") }
    val orientation = LocalConfiguration.current.orientation

    // Funkcia na zmenu jazyka
    fun switchLanguage(languageCode: String) {
        if (languageCode != currentLanguage) {
            setLocale(context, languageCode)
            currentLanguage = languageCode
            Toast.makeText(context, context.getString(R.string.language_settings), Toast.LENGTH_SHORT).show()
        }
    }

    fun updateNTPServer() {
        isButtonNTPLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiService.sendMessage("{\"com\":\"nt\",\"sta\":$isButtonNTPLoading}")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.error) + " ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isButtonNTPLoading = false
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun sendCurrentTime() {
        isButtonTimeLoading = true
        val currentTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(Date())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiService.sendMessage("{\"com\":\"st\",\"time\":\"$currentTime\"}")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.error) + " ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isButtonTimeLoading = false
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun sendDeleteLog() {
        isDeleteLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiService.sendMessage("{\"com\":\"dl\",\"sta\":$isDeleteLoading}")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.error) + " ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isButtonTimeLoading = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(), // Row vyplní celú šírku
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = (stringResource(R.string.settings)),
                            modifier = Modifier.weight(1f) // Posunie ostatné elementy doprava
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
            // First Card
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
                    // Title
                    Text(
                        text = stringResource(R.string.time_setings),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buttons with descriptions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = { updateNTPServer() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isButtonNTPLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isButtonNTPLoading) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(stringResource(R.string.NTP_setting))
                            }
                            Text(
                                text = stringResource(R.string.NTP_description),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = { sendCurrentTime() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isButtonTimeLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isButtonTimeLoading) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(stringResource(R.string.cellular_time))
                            }
                            Text(
                                text = stringResource(R.string.cellular_description),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            // Second Card
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
                        text = stringResource(R.string.log_file),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Button(onClick = {
                            sendDeleteLog()
                            Toast.makeText(
                                context,
                                context.getString(R.string.deleted_log),
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Text(stringResource(R.string.delete_log))
                        }
                    }
                }
            }
            // Second Card
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
                        text = stringResource(R.string.set_device_adress),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        TextField(
                            value = serverAddress,
                            onValueChange = { serverAddress = it },
                            label = { Text(stringResource(R.string.server_address)) }
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Button(onClick = {
                            stopNetworkOperations()
                            ApiService.updateWebSocketUrl(serverAddress)
                            startNetworkOperations()
                            Toast.makeText(
                                context,
                                context.getString(R.string.address_change),
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Text(stringResource(R.string.save))
                        }
                    }
                }
            }
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
                        text = stringResource(R.string.language),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { switchLanguage("sk") },
                            enabled = currentLanguage != "sk"
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.flag_sk),
                                contentDescription = stringResource(R.string.slovak),
                                modifier = Modifier.size(if (selectedLanguage == "sk") 64.dp else 48.dp)
                            )
                        }
                        IconButton(
                            onClick = { switchLanguage("gb") },
                            enabled = currentLanguage != "gb"
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.flag_gb),
                                contentDescription = stringResource(R.string.english),
                                modifier = Modifier.size(if (selectedLanguage == "sk") 64.dp else 48.dp)
                            )
                        }
                        IconButton(
                            onClick = { switchLanguage("de") },
                            enabled = currentLanguage != "de"
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.flag_de),
                                contentDescription = stringResource(R.string.german),
                                modifier = Modifier.size(if (selectedLanguage == "sk") 64.dp else 48.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun setLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = context.resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    // Uloženie preferovaného jazyka do SharedPreferences
    context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        .edit()
        .putString("language", languageCode)
        .apply()

    // Aktualizácia zdrojov a reštart aktivity
    val activity = context as? Activity
    activity?.recreate()
}


fun stopNetworkOperations() {
    // Implementujte logiku na zastavenie napr. prerušením opakovaných úloh alebo zatvorením spojení.
    ApiService.closeWebSocket()
}

fun startNetworkOperations() {
    // Spustite opakované úlohy alebo obnovte činnosť na sieti.
    ApiService.connectWebSocket(
        onClosed = { reason ->
            Log.d("WebSocket", "Closed: $reason")
        },
        onFailure = { error ->
            Log.e("WebSocket", "Error: ${error.message}")
        }
    )
}

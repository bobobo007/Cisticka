package com.example.cisticka.ui.theme

import android.util.Log
import okhttp3.*
import java.util.concurrent.TimeUnit
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object ApiService {
    private var WEBSOCKET_URL: String = "ws://192.168.2.89/ws"
//        private set

    fun getWebSocketUrl(): String = WEBSOCKET_URL

    fun updateWebSocketUrl(newUrl: String) {
        WEBSOCKET_URL = newUrl
        webSocket = null // Reset WebSocket pre nové pripojenie
    }

    private var webSocket: WebSocket? = null
    val isWebSocketConnected = MutableStateFlow(false)
    private val _webSocketData = MutableStateFlow(WebSocketData())
    val webSocketData: StateFlow<WebSocketData> = _webSocketData
    private val _logsf = MutableStateFlow<List<String>>(emptyList())
    val logsf: StateFlow<List<String>> = _logsf

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()
    }

    fun connectWebSocket(
        onClosed: (String) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        if (webSocket != null) {
            Log.d("WebSocket", "WebSocket už je pripojený.")
            return
        }
        val request = Request.Builder().url(WEBSOCKET_URL).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                CoroutineScope(Dispatchers.Main).launch {
                    isWebSocketConnected.emit(true)
                    webSocket.send("{\"com\":\"gv\",\"sta\":true}")  // Požiadavka na existujúce logy
                }
                Log.d("WebSocket", "Pripojené")
                // Pri úspešnom pripojení
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Parsovanie JSON správy na objekt WebSocketData
                try {
                    val gson = Gson()
                    val jsonObject = gson.fromJson(text, JsonObject::class.java)

                    val currentData = _webSocketData.value
                    Log.d("WebSocket", "CurrentData: $currentData")
                    val updatedData = currentData.copy(
                        so = jsonObject["so"]?.asString ?: currentData.so,
                        ha = jsonObject["ha"]?.asString ?: currentData.ha,
                        te = jsonObject["te"]?.asFloat ?: currentData.te,
                        de = jsonObject["de"]?.asInt ?: currentData.de,
                        wi = jsonObject["wi"]?.asInt ?: currentData.wi,
                        cl = jsonObject["cl"]?.asBoolean ?: currentData.cl,
                        fl = jsonObject["fl"]?.asBoolean ?: currentData.fl,
                        ip = jsonObject["ip"]?.asJsonArray?.map { it.asBoolean } ?: currentData.ip,
                        ou = jsonObject["ou"]?.asJsonArray?.map { it.asBoolean } ?: currentData.ou
                    )
                    if (jsonObject.has("lf")) {
                        val logsArray = jsonObject["lf"].asJsonArray.map { it.asString }
                        _logsf.value = logsArray
                        Log.d("WebSocket", "LogUpdated: $logsArray")
                    } else {
                        _webSocketData.value = updatedData // Aktualizácia MutableStateFlow
                        Log.d("WebSocket", "Updated: $updatedData")
                        }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error parsing message: ${e.message}")
                }
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Pri chybe
                Log.e("WebSocket", "Chyba: ${t.message}")
                CoroutineScope(Dispatchers.Main).launch {
                    isWebSocketConnected.emit(false)
                }
                onFailure(t)
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Zatvorené: $reason")
                CoroutineScope(Dispatchers.Main).launch {
                    isWebSocketConnected.emit(false)
                }
                onClosed(reason)
            }
        })
    }

    data class WebSocketData(
        val so: String = "V00.000", // Software version
        val ha: String = "V00.000", // Hardware version
        val te: Float = 0f,         // Temperature
        val de: Int = 0,            // Depth
        val wi: Int = 0,            // WiFi RSSI
        val cl: Boolean = false,    // Clean status
        val fl: Boolean = false,    // flag Cleaning On
        val ip: List<Boolean> = listOf(false, false, false, false), // Inputs
        val ou: List<Boolean> = listOf(false, false, false, false)  // Outputs
    )

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun closeWebSocket() {
        webSocket?.close(1000, "Client disconnected")
        webSocket = null
        CoroutineScope(Dispatchers.Main).launch {
            isWebSocketConnected.emit(false)
        }
    }
}

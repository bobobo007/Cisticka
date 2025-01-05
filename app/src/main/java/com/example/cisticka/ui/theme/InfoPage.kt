package com.example.cisticka.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cisticka.R
import com.example.cisticka.ui.theme.ApiService.WebSocketData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoPage(navigateBack: () -> Unit, webSocketData: WebSocketData, isWebSocketConnected: Boolean) {
    val orientation = LocalConfiguration.current.orientation

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(), // Row vyplní celú šírku
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.info_page),
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
            CardWithDynamicText(
                title = stringResource(R.string.values),
                lines = listOf(
                    stringResource(R.string.temperature) to "${webSocketData.te}°C",
                    stringResource(R.string.depth) to "${webSocketData.de}mm",
                    stringResource(R.string.wifi) to "${webSocketData.wi}dB",
                    stringResource(R.string.hardware) to webSocketData.ha,
                    stringResource(R.string.software) to webSocketData.so
                )
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start // Zarovná prvky v rade doprava
                    ) {
                        Text(
                            text = stringResource(R.string.information),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = 8.dp) // Pridá malé odsadenie od okraja
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.pcb),
                        contentDescription = "Informative Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = stringResource(R.string.info_text),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CardWithDynamicText(
    title: String,
    lines: List<Pair<String, String>>
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

@Preview(showBackground = true)
@Composable
fun PreviewInfoPage() {
    CistickaTheme {
        InfoPage(navigateBack = {},
            webSocketData = WebSocketData(),
            isWebSocketConnected = true)
    }
}

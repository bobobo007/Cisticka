package com.example.cisticka.ui.theme

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cisticka.R
import com.example.cisticka.ui.theme.ApiService.WebSocketData
import androidx.core.net.toUri


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoPage(navigateBack: () -> Unit, webSocketData: WebSocketData, isWebSocketConnected: Boolean) {
    val orientation = LocalConfiguration.current.orientation

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.info_page),
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
            CardWithDynamicText(
                title = stringResource(R.string.values),
                lines = listOf(
                    stringResource(R.string.temperature) to "${webSocketData.te}°C",
                    stringResource(R.string.depth) to "${webSocketData.de}mm",
                    stringResource(R.string.wifi) to "${webSocketData.wi}dB",
                    stringResource(R.string.hardware) to webSocketData.ha,
                    stringResource(R.string.software) to webSocketData.so,
                    stringResource(R.string.application) to "V06.011"
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
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = stringResource(R.string.information),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = 8.dp)
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
                    InfoWithHyperlink()
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


@Composable
fun InfoWithHyperlink() {
    val context = LocalContext.current
    val annotatedString = buildAnnotatedString {
        append(stringResource(R.string.info_text))
        pushStringAnnotation(tag = "URL", annotation = "https://oshwlab.com/bobobo007/cisticka-_v06-001/")
        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
            append("https://oshwlab.com/bobobo007/cisticka-_v06-001/")
        }
        pop()
    }
    BasicText(
        text = annotatedString,
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                annotatedString.getStringAnnotations(tag = "URL", start = 0, end = annotatedString.length).firstOrNull()?.let {
                    val intent = Intent(Intent.ACTION_VIEW, it.item.toUri())
                    context.startActivity(intent)
                }
            },
        style = MaterialTheme.typography.bodyMedium
    )
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

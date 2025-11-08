package com.example.bitnow.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bitnow.presentation.theme.BitNowTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BitNowTheme {
                BitcoinPriceScreen()
            }
        }
    }
}

@Composable
fun BitcoinPriceScreen() {
    var price by remember { mutableStateOf("Buscando...") }
    var isUpdating by remember { mutableStateOf(false) }

    val orangeColor = Color(0xFFFF9800)
    val backgroundColor = Color.Black

    LaunchedEffect(Unit) {
        while (true) {
            try {
                isUpdating = true
                val btcPrice = fetchBitcoinPrice()
                price = "$${String.format("%.2f", btcPrice)}"
            } catch (e: Exception) {
                e.printStackTrace()
                price = "Erro ao buscar preço"
            } finally {
                isUpdating = false
            }
            delay(15_000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "1 Bitcoin",
                color = orangeColor,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                price,
                color = orangeColor,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isUpdating) {
                Text(
                    text = "Atualizando...",
                    color = orangeColor.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

suspend fun fetchBitcoinPrice(): Double = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd")
        .build()
    val response = client.newCall(request).execute()
    val json = JSONObject(response.body?.string() ?: "{}")
    json.getJSONObject("bitcoin").getDouble("usd")
}

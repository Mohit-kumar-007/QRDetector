package com.example.qrdetcter.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.qrdetcter.Network.SafeBrowsingApi
import com.example.qrdetcter.Network.SafeBrowsingRequest
import com.example.qrdetcter.Network.ThreatEntry
import com.example.qrdetcter.Network.ThreatInfo
import com.example.qrdetcter.QrScannerScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    var scannedText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Scan a QR Code", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        QrScannerScreen { result ->
            scannedText = result
            coroutineScope.launch {
                checkUrlSafety(result, context)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (scannedText.isNotEmpty()) {
            Text(text = "Scanned Data: $scannedText", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// Function to Check URL Safety
suspend fun checkUrlSafety(url: String, context: Context) {
    try {
        val response = SafeBrowsingApi.service.checkUrl(
            SafeBrowsingRequest(
                client = mapOf("clientId" to "your-app", "clientVersion" to "1.0"),
                threatInfo = ThreatInfo(threatEntries = listOf(ThreatEntry(url)))
            )
        )

        if (response.matches != null) {
            Toast.makeText(context, "⚠️ Unsafe URL Detected! Do not open.", Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        Log.e("SafeBrowsing", "Error checking URL", e)
        Toast.makeText(context, "Error checking URL safety", Toast.LENGTH_SHORT).show()
    }
}

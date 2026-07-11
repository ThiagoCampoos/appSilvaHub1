package com.example.silvahub.util

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.silvahub.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val openNovoGasto = intent?.getBooleanExtra(EXTRA_OPEN_NOVO_GASTO, false) == true ||
            intent?.action == ACTION_NOVO_GASTO ||
            intent?.extras?.containsKey("open_novo_gasto") == true

        setContent {
            App(openNovoGasto = openNovoGasto)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recreate()
    }

    companion object {
        const val EXTRA_OPEN_NOVO_GASTO = "open_novo_gasto"
        const val ACTION_NOVO_GASTO = "com.example.silvahub.ACTION_NOVO_GASTO"
    }
}

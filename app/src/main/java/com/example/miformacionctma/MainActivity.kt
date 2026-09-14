@file:Suppress("SpellCheckingInspection")

package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.miformacionctma.ui.AppNavigation
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme
import com.example.miformacionctma.worker.NotificacionWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        programarNotificaciones()

        setContent {
            MiFormacionCTMATheme {
                AppNavigation()
            }
        }
    }

    private fun programarNotificaciones() {
        val workRequest = PeriodicWorkRequestBuilder<NotificacionWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "notificacion_vencimiento",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

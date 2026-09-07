package com.example.miformacionctma.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotificacionWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // En una app real, aquí consultaríamos la base de datos.
        // Simulamos que encontramos una actividad próxima a vencer (diasRestantes <= 0 o 1).
        
        mostrarNotificacion(
            "¡Atención!",
            "Tienes actividades que vencen en menos de 24 horas. ¡No las olvides!",
        )

        return Result.success()
    }

    private fun mostrarNotificacion(titulo: String, mensaje: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "vencimiento_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Vencimiento",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}

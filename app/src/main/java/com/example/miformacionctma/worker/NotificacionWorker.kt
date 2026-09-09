package com.example.miformacionctma.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.miformacionctma.ActividadesApplication
import com.example.miformacionctma.domain.ReglasActividad
import kotlinx.coroutines.flow.first

class NotificacionWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val application = applicationContext as ActividadesApplication
        val repository = application.actividadRepository
        
        try {
            val actividades = repository.observarTodos().first()
            val urgentes = ReglasActividad.actividadesUrgentes(actividades)
            
            if (urgentes.isNotEmpty()) {
                val mensaje = "Tienes ${urgentes.size} actividades próximas a vencer."
                mostrarNotificacion("Recordatorio CTMA", mensaje)
            }
            
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun mostrarNotificacion(titulo: String, mensaje: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "vencimiento_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Vencimiento",
                NotificationManager.IMPORTANCE_HIGH,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(101, notification)
    }
}

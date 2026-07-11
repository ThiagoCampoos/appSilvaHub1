package com.example.silvahub.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.silvahub.domain.repository.ContaFixaRepository
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ContasVencimentoWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val repository = GlobalContext.get().get<ContaFixaRepository>()
        val hoje = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val contas = repository.getContasFixasAtivas().first().filter { it.diaVencimento == hoje }
        if (contas.isEmpty()) return Result.success()

        createChannel(applicationContext)
        val canNotify = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

        if (!canNotify) return Result.success()

        contas.forEachIndexed { index, conta ->
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Conta vence hoje")
                .setContentText("${conta.nome} — R$ ${"%.2f".format(conta.valor)}")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            NotificationManagerCompat.from(applicationContext)
                .notify(1000 + index, notification)
        }
        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "contas_vencimento"
        const val WORK_NAME = "contas_vencimento_daily"

        fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Vencimento de contas",
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
                val manager = context.getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(channel)
            }
        }

        fun schedule(context: Context) {
            createChannel(context)
            val request = PeriodicWorkRequestBuilder<ContasVencimentoWorker>(1, TimeUnit.DAYS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}

package com.example.mambappv2.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.mambappv2.data.sync.SyncManager
import com.example.mambappv2.di.AppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "SyncWorker"
        const val WORK_NAME = "sync_work"
        
        /**
         * Programa trabajo de sincronización periódica
         */
        fun schedulePeriodicSync(context: Context, intervalMinutes: Long = 30) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                intervalMinutes, TimeUnit.MINUTES,
                15, TimeUnit.MINUTES // Ventana de flexibilidad
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    15, TimeUnit.MINUTES
                )
                .addTag(TAG)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest
                )
        }
        
        /**
         * Programa sincronización única inmediata
         */
        fun scheduleImmediateSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .addTag(TAG)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "immediate_sync",
                    ExistingWorkPolicy.REPLACE,
                    syncRequest
                )
        }
        
        /**
         * Cancela todos los trabajos de sincronización
         */
        fun cancelAllSyncWork(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Iniciando sincronización en background")
            
            // Obtener dependencias (normalmente inyectadas)
            val appContainer = AppContainer.getInstance(applicationContext)
            val syncManager = appContainer.syncManager
            
            // Ejecutar sincronización
            val result = syncManager.performQuickSync()
            
            if (result.isSuccess) {
                Log.d(TAG, "Sincronización completada exitosamente")
                Result.success()
            } else {
                Log.e(TAG, "Error en sincronización: ${result.exceptionOrNull()?.message}")
                Result.retry()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error ejecutando sincronización en background", e)
            
            // Decidir si reintentar basado en el tipo de error
            when {
                e.message?.contains("network", ignoreCase = true) == true -> Result.retry()
                e.message?.contains("auth", ignoreCase = true) == true -> Result.failure()
                runAttemptCount < 3 -> Result.retry()
                else -> Result.failure()
            }
        }
    }
} 
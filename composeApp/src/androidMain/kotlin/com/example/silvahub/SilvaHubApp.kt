package com.example.silvahub

import android.app.Application
import com.example.silvahub.di.appModule
import com.example.silvahub.domain.usecase.GerarCobrancasRecorrentesUseCase
import com.example.silvahub.notifications.ContasVencimentoWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SilvaHubApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SilvaHubApp)
            modules(appModule)
        }
        ContasVencimentoWorker.schedule(this)
        appScope.launch {
            runCatching { get<GerarCobrancasRecorrentesUseCase>().invoke() }
        }
    }
}

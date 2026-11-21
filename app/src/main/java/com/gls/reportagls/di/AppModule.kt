package com.gls.reportagls.di

import android.content.Context
import com.gls.reportagls.data.ReportRepository
import com.gls.reportagls.data.SyncManager
import com.gls.reportagls.data.UserRepository
import com.gls.reportagls.data.local.ReportLocalDataSource
import com.gls.reportagls.data.local.UserLocalDataSource
import com.gls.reportagls.data.remote.MockApiInterceptor
import com.gls.reportagls.data.remote.MockBackend
import com.gls.reportagls.data.remote.ReportApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Contenedor de dependencias sencillo (service locator).
 *
 * Centraliza repositorios, el cliente REST y el administrador de sincronía.
 * Para usar el servicio real basta con sustituir el OkHttpClient (quitar el
 * interceptor simulado) y conservar el mismo [ReportApiService].
 */
class AppModule(context: Context) {
    private val appContext = context.applicationContext

    val reportRepository: ReportRepository by lazy {
        ReportRepository(ReportLocalDataSource(appContext))
    }

    val userRepository: UserRepository by lazy {
        UserRepository(UserLocalDataSource(appContext))
    }

    val mockBackend = MockBackend()

    val reportApiService: ReportApiService by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(MockApiInterceptor(mockBackend))
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.reportagls.local/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReportApiService::class.java)
    }

    val syncManager: SyncManager by lazy {
        SyncManager(appContext, reportRepository, reportApiService, mockBackend)
    }
}

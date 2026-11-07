package com.gls.reportagls.di

import android.content.Context
import com.gls.reportagls.data.ReportRepository
import com.gls.reportagls.data.UserRepository
import com.gls.reportagls.data.local.ReportLocalDataSource
import com.gls.reportagls.data.local.UserLocalDataSource

/**
 * Contenedor de dependencias sencillo (service locator).
 *
 * Mantiene la creación de repositorios en un solo lugar para que la UI y el
 * ViewModel no dependan de implementaciones concretas. Si el proyecto crece se
 * puede sustituir por Hilt sin cambiar los puntos de uso.
 */
class AppModule(context: Context) {
    private val appContext = context.applicationContext

    val reportRepository: ReportRepository by lazy {
        ReportRepository(ReportLocalDataSource(appContext))
    }

    val userRepository: UserRepository by lazy {
        UserRepository(UserLocalDataSource(appContext))
    }
}

package com.gls.reportagls

import android.app.Application
import com.gls.reportagls.di.AppModule

/** Application de ReportaGLS: expone el contenedor de dependencias. */
class ReportaGlsApplication : Application() {

    lateinit var appModule: AppModule
        private set

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this)
    }
}

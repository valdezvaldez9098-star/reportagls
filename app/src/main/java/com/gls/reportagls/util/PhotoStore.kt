package com.gls.reportagls.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

/**
 * Guarda y recupera las fotografías de los reportes en el almacenamiento interno
 * de la app. Se usa el almacenamiento interno para no depender de permisos de
 * almacenamiento externo y para que el reporte funcione sin conexión.
 */
object PhotoStore {

    private fun photosDir(context: Context): File =
        File(context.filesDir, "report_photos").apply { mkdirs() }

    fun save(context: Context, bitmap: Bitmap): String {
        val file = File(photosDir(context), "report_${System.currentTimeMillis()}.png")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        }
        return file.absolutePath
    }

    fun load(path: String?): Bitmap? {
        if (path.isNullOrBlank()) return null
        val file = File(path)
        return if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }
}

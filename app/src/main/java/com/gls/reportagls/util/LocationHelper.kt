package com.gls.reportagls.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.gls.reportagls.domain.LatLng

/**
 * Acceso sencillo a la última ubicación conocida del dispositivo usando solo
 * APIs del sistema (LocationManager), sin dependencias de Google Play Services.
 */
object LocationHelper {

    fun hasPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    fun lastKnownLocation(context: Context): LatLng? {
        if (!hasPermission(context)) return null
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null
        val providers = runCatching { manager.getProviders(true) }.getOrDefault(emptyList())
        var best: Location? = null
        for (provider in providers) {
            val location = runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
            if (location != null && (best == null || location.time > best.time)) {
                best = location
            }
        }
        return best?.let { LatLng(it.latitude, it.longitude) }
    }
}

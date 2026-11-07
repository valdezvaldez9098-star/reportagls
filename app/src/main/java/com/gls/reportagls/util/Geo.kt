package com.gls.reportagls.util

import com.gls.reportagls.domain.LatLng
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_METERS = 6_371_000.0

/** Distancia en metros entre dos coordenadas usando la fórmula de Haversine. */
fun distanceMeters(from: LatLng, to: LatLng): Double {
    val dLat = Math.toRadians(to.latitude - from.latitude)
    val dLon = Math.toRadians(to.longitude - from.longitude)
    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(Math.toRadians(from.latitude)) * cos(Math.toRadians(to.latitude)) *
        sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return EARTH_RADIUS_METERS * c
}

fun distanceLabel(meters: Double): String =
    if (meters < 1000) {
        "${meters.toInt()} m"
    } else {
        String.format(Locale.getDefault(), "%.1f km", meters / 1000.0)
    }

fun formatDateTime(millis: Long): String =
    java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(millis)

fun formatDate(millis: Long): String =
    java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(millis)

fun formatTime(millis: Long): String =
    java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(millis)

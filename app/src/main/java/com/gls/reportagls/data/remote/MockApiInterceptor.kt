package com.gls.reportagls.data.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException
import java.lang.reflect.Type

/** Interceptor que responde como si existiera un servicio REST reportagls.local. */
class MockApiInterceptor(private val backend: MockBackend) : Interceptor {

    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Thread.sleep(300)

        if (backend.forceOffline) {
            throw IOException("Sin conexión (simulada desde la interfaz)")
        }

        val path = request.url.encodedPath.removePrefix("/v1")
        return when {
            path == "/reports" && request.method == "GET" ->
                jsonResponse(request, 200, gson.toJson(backend.snapshot()))

            path == "/reports" && request.method == "POST" -> {
                val report = parse<ReportDto>(request, ReportDto::class.java)
                backend.add(report)
                jsonResponse(request, 201, gson.toJson(report))
            }

            path.startsWith("/reports/") && path.endsWith("/status") &&
                request.method == "PATCH" -> {
                val id = path.removePrefix("/reports/").removeSuffix("/status")
                val body = parse<Map<String, String>>(
                    request,
                    object : TypeToken<Map<String, String>>() {}.type
                )
                val updated = backend.updateStatus(id, body["status"].orEmpty())
                if (updated == null) {
                    jsonResponse(request, 404, "{\"error\":\"Reporte no encontrado\"}")
                } else {
                    jsonResponse(request, 200, gson.toJson(updated))
                }
            }

            else -> jsonResponse(request, 200, "[]")
        }
    }

    private fun <T> parse(request: Request, type: Type): T {
        val buffer = Buffer()
        request.body?.writeTo(buffer)
        return gson.fromJson(buffer.readUtf8(), type)
    }

    private fun jsonResponse(request: Request, code: Int, json: String): Response =
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code == 201) "Created" else "OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .build()
}

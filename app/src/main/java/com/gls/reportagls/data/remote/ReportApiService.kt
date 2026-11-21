package com.gls.reportagls.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST

/** Contrato REST consumido por SyncManager. */
interface ReportApiService {

    @GET("reports")
    suspend fun getReports(): List<ReportDto>

    @POST("reports")
    suspend fun createReport(@Body report: ReportDto): ReportDto

    @PATCH("reports/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): ReportDto
}

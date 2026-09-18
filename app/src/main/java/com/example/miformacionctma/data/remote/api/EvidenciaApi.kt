package com.example.miformacionctma.data.remote.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface EvidenciaApi {
    @Multipart
    @POST("v1/actividades/{id}/evidencias")
    suspend fun subirEvidencia(
        @Path("id") actividadId: Long,
        @Part imagen: MultipartBody.Part,
        @Header("Idempotency-Key") idempotencyKey: String
    ): Response<Unit>
}

package com.example.miformacionctma.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class EvidenciaResponseDto(
    val id: Long,
    val url: String,
    val nombreArchivo: String,
    val subidoEn: String
)

interface EvidenciaApiService {
    @Multipart
    @POST("evidencias/upload")
    suspend fun subirEvidencia(
        @Part("actividadId") actividadId: RequestBody,
        @Part file: MultipartBody.Part
    ): EvidenciaResponseDto
}

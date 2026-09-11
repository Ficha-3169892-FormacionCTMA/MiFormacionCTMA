package com.example.miformacionctma.data.remote.dto

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.EstadoActividad
import com.example.miformacionctma.domain.Prioridad
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO (Data Transfer Object) para representar la actividad en el servicio remoto (Supabase/REST).
 * Separa el contrato de red de la lógica de dominio (Semana 8).
 */
@Serializable
data class ActividadDto(
    @SerialName("id")
    val id: Long,
    @SerialName("titulo")
    val titulo: String,
    @SerialName("descripcion")
    val descripcion: String? = null,
    @SerialName("progreso")
    val progreso: Int,
    @SerialName("dias_restantes")
    val diasRestantes: Int,
    @SerialName("estado")
    val estado: String,
    @SerialName("prioridad")
    val prioridad: String,
    @SerialName("horas")
    val horas: Int,
    @SerialName("enlace_evidencia")
    val enlaceEvidencia: String? = null
)

// Mapeadores
fun ActividadFormativa.toDto() = ActividadDto(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    progreso = progreso,
    diasRestantes = diasRestantes,
    estado = estado.name,
    prioridad = prioridad.name,
    horas = horas,
    enlaceEvidencia = enlaceEvidencia
)

fun ActividadDto.toDomain() = ActividadFormativa(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    progreso = progreso,
    diasRestantes = diasRestantes,
    estado = EstadoActividad.valueOf(estado),
    prioridad = Prioridad.valueOf(prioridad),
    horas = horas,
    enlaceEvidencia = enlaceEvidencia
)

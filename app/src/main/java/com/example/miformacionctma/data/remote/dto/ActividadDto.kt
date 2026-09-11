package com.example.miformacionctma.data.remote.dto

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO Minimalista para garantizar compatibilidad con la tabla actual en Supabase.
 * Solo enviamos los campos esenciales.
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
    @SerialName("prioridad")
    val prioridad: String,
)

// Mapeadores que aseguran que los datos locales no se pierdan aunque no se suban todos a la nube
fun ActividadFormativa.toDto() = ActividadDto(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    progreso = progreso,
    diasRestantes = diasRestantes,
    prioridad = prioridad.name,
)

fun ActividadDto.toDomain() = ActividadFormativa(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    progreso = progreso,
    diasRestantes = diasRestantes,
    // Recuperamos/Calculamos los campos que no viajan a la nube (Semana 8)
    estado = ReglasActividad.obtenerEstado(progreso, diasRestantes),
    prioridad = Prioridad.valueOf(prioridad),
    horas = 10, // Valor por defecto local
    enlaceEvidencia = null, // Se mantiene localmente
)

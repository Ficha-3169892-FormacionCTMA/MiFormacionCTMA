package com.example.miformacionctma.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActividadFormativa(
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
    val estado: EstadoActividad,
    
    @SerialName("prioridad")
    val prioridad: Prioridad = Prioridad.MEDIA,
    
    @SerialName("horas")
    val horas: Int = 10,
    
    @SerialName("enlace_evidencia")
    val enlaceEvidencia: String? = null,
)

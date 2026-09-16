package com.example.miformacionctma.ui.viewmodel

import android.content.ContextWrapper
import com.example.miformacionctma.data.local.dao.EvidenciaDao
import com.example.miformacionctma.data.local.entities.EvidenciaEntity
import com.example.miformacionctma.data.remote.EvidenciaApiService
import com.example.miformacionctma.data.remote.EvidenciaResponseDto
import com.example.miformacionctma.data.repository.EvidenciaRepository
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import com.example.miformacionctma.domain.PreferenciasRepository
import com.example.miformacionctma.domain.PreferenciasUsuario
import com.example.miformacionctma.ui.states.ListadoUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActividadesViewModelTest {

    private lateinit var viewModel: ActividadesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeRepository = object : ActividadRepository {
        override fun observarTodos(): Flow<List<ActividadFormativa>> = flowOf(emptyList())
        override fun observarPorId(id: Long): Flow<ActividadFormativa?> = flowOf(null)
        override fun buscar(texto: String): Flow<List<ActividadFormativa>> = flowOf(emptyList())
        override suspend fun guardar(actividad: ActividadFormativa) {}
        override suspend fun eliminar(id: Long): Boolean = true
    }

    private val fakePreferenciasRepository = object : PreferenciasRepository {
        override val preferencias: Flow<PreferenciasUsuario> = flowOf(PreferenciasUsuario())
        override suspend fun guardarFiltroPrioridad(prioridad: com.example.miformacionctma.domain.Prioridad?) {}
        override suspend fun guardarOrdenadoPorVencimiento(ordenado: Boolean) {}
        override suspend fun guardarModoCuadricula(activo: Boolean) {}
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val dummyDao = object : EvidenciaDao {
            override suspend fun insertarEvidencia(evidencia: EvidenciaEntity): Long = 0L
            override suspend fun actualizarEvidencia(evidencia: EvidenciaEntity) {}
            override fun obtenerEvidenciasPorActividad(actividadId: Long): Flow<List<EvidenciaEntity>> =
                flowOf(emptyList())
        }
        val dummyApi = object : EvidenciaApiService {
            override suspend fun subirEvidencia(actividadId: RequestBody, file: MultipartBody.Part): EvidenciaResponseDto {
                return EvidenciaResponseDto(0L, "", "", "")
            }
        }
        val fakeEvidenciaRepository = EvidenciaRepository(
            context = ContextWrapper(null),
            evidenciaDao = dummyDao,
            apiService = dummyApi
        )
        viewModel = ActividadesViewModel(fakeRepository, fakePreferenciasRepository, fakeEvidenciaRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_iniciaConCargando() = runTest {
        assertTrue(viewModel.uiState.value is ListadoUiState.Cargando)
    }
}

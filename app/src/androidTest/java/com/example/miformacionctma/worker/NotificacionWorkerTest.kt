package com.example.miformacionctma.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificacionWorkerTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testNotificacionWorker_doWork_returnsSuccess_HU14() {
        val worker = TestListenableWorkerBuilder<NotificacionWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertEquals(ListenableWorker.Result.success(), result)
        }
    }
}

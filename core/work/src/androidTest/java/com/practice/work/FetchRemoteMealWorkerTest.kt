package com.practice.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.practice.combine.toMealEntity
import com.practice.meal.FakeMealDataSource
import com.practice.meal.MealRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FetchRemoteMealWorkerTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val localRepository =
        MealRepository(FakeMealDataSource())
    private val remoteRepository =
        com.practice.api.meal.RemoteMealRepository(com.practice.api.meal.FakeRemoteMealDataSource())
    private val preferencesRepository: com.practice.preferences.PreferencesRepository =
        com.practice.preferences.FakePreferencesRepository()

    @Test
    fun doWork(): Unit = runBlocking {
        val months = (1..12)
        val remoteMeals = months.map { month ->
            remoteRepository.getMeals(2022, month).response
        }.flatten().map { it.toMealEntity() }

        val worker = buildWorker()
        val result = worker.doWork()
        assertThat(result).isEqualTo(ListenableWorker.Result.success())

        val storedMeals = months.map { month ->
            localRepository.getMeals(2022, month).first()
        }.flatten()
        assertThat(remoteMeals).isNotEmpty
            .containsExactlyInAnyOrderElementsOf(storedMeals)
    }

    private fun buildWorker(): FetchRemoteMealWorker =
        TestListenableWorkerBuilder<FetchRemoteMealWorker>(context)
            .setWorkerFactory((object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker {
                    return FetchRemoteMealWorker(
                        context,
                        workerParameters,
                        localRepository,
                        remoteRepository,
                        preferencesRepository
                    )
                }
            })).build()
}
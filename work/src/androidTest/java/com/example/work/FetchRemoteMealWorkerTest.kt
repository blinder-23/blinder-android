package com.example.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.domain.combine.toMealEntity
import com.practice.database.meal.FakeMealDataSource
import com.practice.database.meal.MealRepository
import com.practice.neis.meal.FakeRemoteMealDataSource
import com.practice.neis.meal.RemoteMealRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FetchRemoteMealWorkerTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val localRepository = MealRepository(FakeMealDataSource())
    private val remoteRepository = RemoteMealRepository(FakeRemoteMealDataSource())

    @Test
    fun doWork(): Unit = runBlocking {
        val months = (1..12)
        val remoteMeals = months.map { month ->
            remoteRepository.getMealData(2022, month)
        }.flatten().map { it.toMealEntity() }

        val worker = buildWorker()
        val result = worker.doWork()
        assertThat(result).isEqualTo(ListenableWorker.Result.success())

        val storedMeals = months.map { month ->
            localRepository.getMeals(2022, month)
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
                        remoteRepository
                    )
                }
            })).build()
}
package com.practice.preferences

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

interface PreferencesRepository : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    val userPreferencesFlow: StateFlow<UserPreferences>

    suspend fun updateUiMode(uiMode: UiMode)
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateIsFirstExecution(isFirstExecution: Boolean)
    suspend fun updateScreenMode(screenMode: ScreenMode)
    suspend fun increaseRunningWorkCount()
    suspend fun decreaseRunningWorkCount()
    suspend fun updateSelectedSchool(schoolCode: Int, schoolName: String)
    suspend fun getAndIncreaseMemoIdCount(): Int
    suspend fun clear()
    suspend fun fetchInitialPreferences(): UserPreferences
}
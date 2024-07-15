package com.thedearbear.nnov

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore("settings")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext appContext: Context
) {
    private val settings = appContext.settingsDataStore

    private val lastUsedAccountKey = intPreferencesKey("last_used_account")

    private val showLessonsOnVacationKey = booleanPreferencesKey("show_lessons_on_vacation")
    private val showStartEndTimeOfLessonKey = booleanPreferencesKey("show_start_end_time_of_lesson")
    private val showPreviewTextKey = booleanPreferencesKey("show_preview_text")

    val lastUsedAccount: Flow<Int> = settings.data.map { preferences ->
        preferences[lastUsedAccountKey] ?: -1
    }

    val showLessonsOnVacation: Flow<Boolean> = settings.data.map { preferences ->
        preferences[showLessonsOnVacationKey] ?: false
    }

    val showStartEndTimeOfLesson: Flow<Boolean> = settings.data.map { preferences ->
        preferences[showStartEndTimeOfLessonKey] ?: true
    }

    val showPreviewText: Flow<Boolean> = settings.data.map { preferences ->
        preferences[showPreviewTextKey] ?: true
    }

    suspend fun setLastUsedAccount(id: Int) {
        settings.edit { preferences ->
            preferences[lastUsedAccountKey] = id
        }
    }

    suspend fun setShowLessonsOnVacation(show: Boolean) {
        settings.edit { preferences ->
            preferences[showLessonsOnVacationKey] = show
        }
    }

    suspend fun setShowStartEndTimeOfLesson(show: Boolean) {
        settings.edit { preferences ->
            preferences[showStartEndTimeOfLessonKey] = show
        }
    }

    suspend fun setShowPreviewText(show: Boolean) {
        settings.edit { preferences ->
            preferences[showPreviewTextKey] = show
        }
    }

    suspend fun cache() {
        settings.data.first()
    }
}
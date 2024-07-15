package com.thedearbear.nnov.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thedearbear.nnov.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AppSettingsViewModel @Inject constructor(
    val handle: SavedStateHandle,
    val manager: DataStoreManager
) : ViewModel() {
    val showLessonsOnVacation = mutableStateOf(_showLessonsOnVacation)
    private var _showLessonsOnVacationBacking: Boolean? = null
    private var _showLessonsOnVacation: Boolean
        get() {
            if (_showLessonsOnVacationBacking == null) {
                _showLessonsOnVacationBacking = runBlocking {
                    manager.showLessonsOnVacation.single()
                }
            }

            return _showLessonsOnVacationBacking as Boolean
        }
        set(value) {
            viewModelScope.launch(Dispatchers.IO) {
                manager.setShowLessonsOnVacation(value)
                _showLessonsOnVacationBacking = value
            }
        }

    val showStartEndTimeOfLesson = mutableStateOf(_showStartEndTimeOfLesson)
    private var _showStartEndTimeOfLessonBacking: Boolean? = null
    private var _showStartEndTimeOfLesson: Boolean
        get() {
            if (_showStartEndTimeOfLessonBacking == null) {
                _showStartEndTimeOfLessonBacking = runBlocking {
                    manager.showStartEndTimeOfLesson.single()
                }
            }

            return _showStartEndTimeOfLessonBacking as Boolean
        }
        set(value) {
            viewModelScope.launch(Dispatchers.IO) {
                manager.setShowStartEndTimeOfLesson(value)
                _showStartEndTimeOfLessonBacking = value
            }
        }

    val showPreviewText = mutableStateOf(_showPreviewText)
    private var _showPreviewTextBacking: Boolean? = null
    private var _showPreviewText: Boolean
        get() {
            if (_showPreviewTextBacking == null) {
                _showPreviewTextBacking = runBlocking {
                    manager.showPreviewText.single()
                }
            }

            return _showPreviewTextBacking as Boolean
        }
        set(value) {
            viewModelScope.launch(Dispatchers.IO) {
                manager.setShowPreviewText(value)
                _showPreviewTextBacking = value
            }
        }
}
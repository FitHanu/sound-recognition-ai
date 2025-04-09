package com.example.jetnews.ui.recorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.classifiers.Classifier
import com.example.jetnews.data.classifiers.ClassifierRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Recorder screen
 */
data class RecorderUiState(
    val classifier: Classifier? = null,
    val isLoading: Boolean = false,
    val isRecording: Boolean = false,
)

class RecorderViewModel(
    private val classifierRepository: ClassifierRepository
) : ViewModel() {

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(RecorderUiState(isLoading = true))
    val uiState: StateFlow<RecorderUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
    }

    /**
     * Refresh topics, people, and publications
     */
    private fun refreshAll() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // Trigger repository requests in parallel
            val classifierDeferred = async { classifierRepository.getClassifier() }

            // Wait for all requests to finish
            val topics = classifierDeferred.await().getOrNull()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    classifier = topics,
                )
            }
        }
    }

    fun toggleRecording() {
        _uiState.update {
            it.copy(
                isRecording = true,
            )
        }
    }

    /**
     * Factory for InterestsViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            classifierRepository: ClassifierRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecorderViewModel(classifierRepository) as T
            }
        }
    }
}

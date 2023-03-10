package xyz.myeoru.kotlinflowexample

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        stateTest()
    }

    fun stateTest() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            delay(1500)
            getString().onCompletion {
                Log.e("테스트", "완료됨")
            }.collect {
                _uiState.value = MainUiState.Success(it)
            }
            delay(1500)
            _uiState.value = MainUiState.Loading
            delay(1500)
            _uiState.value = MainUiState.Error(Throwable("Error Test"))
        }
    }

    private suspend fun getString() = flow {
        emit("StateFlow test")
    }
}

sealed class MainUiState {
    object Loading: MainUiState()
    data class Success(val text: String): MainUiState()
    data class Error(val throwable: Throwable): MainUiState()
}

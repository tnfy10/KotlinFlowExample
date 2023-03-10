package xyz.myeoru.kotlinflowexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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
            getString().collect {
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

    fun callbackFlowTest() {
        viewModelScope.launch {
            getStringCallback().catch {
                _uiState.value = MainUiState.Error(it)
            }.collect {
                _uiState.value = MainUiState.Success(it)
            }
        }
    }

    private fun getStringCallback() = callbackFlow {
        CallbackCaller(object : CallbackListener {
            override fun onResult(result: String) {
                trySend(result)
                    .onSuccess {
                        close()
                    }.onFailure {
                        throw it ?: Throwable("onFailure Throwable is null")
                    }
            }
        }).call()

        awaitClose()
    }
}

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val text: String) : MainUiState()
    data class Error(val throwable: Throwable) : MainUiState()
}

package xyz.myeoru.kotlinflowexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import xyz.myeoru.kotlinflowexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            viewModel.stateTest()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is MainUiState.Loading -> {
                            binding.button.visibility = View.INVISIBLE
                            binding.textView.visibility = View.INVISIBLE
                            binding.loading.visibility = View.VISIBLE
                        }
                        is MainUiState.Success -> {
                            binding.loading.visibility = View.INVISIBLE
                            binding.textView.visibility = View.VISIBLE
                            binding.textView.text = uiState.text
                        }
                        is MainUiState.Error -> {
                            binding.loading.visibility = View.INVISIBLE
                            binding.button.visibility = View.VISIBLE
                            binding.textView.visibility = View.VISIBLE
                            binding.textView.text = uiState.throwable.message
                        }
                    }
                }
            }
        }
    }
}
package xyz.myeoru.kotlinflowexample

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import xyz.myeoru.kotlinflowexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            stateFlowTestBtn.setOnClickListener {
                viewModel.stateTest()
            }

            callbackFlowTestBtn.setOnClickListener {
                viewModel.callbackFlowTest()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is MainUiState.Init -> {
                            binding.apply {
                                stateFlowTestBtn.visibility = View.VISIBLE
                                callbackFlowTestBtn.visibility = View.VISIBLE
                                textView.visibility = View.INVISIBLE
                                loading.visibility = View.INVISIBLE
                            }
                        }
                        is MainUiState.Loading -> {
                            binding.apply {
                                stateFlowTestBtn.visibility = View.INVISIBLE
                                callbackFlowTestBtn.visibility = View.INVISIBLE
                                textView.visibility = View.INVISIBLE
                                loading.visibility = View.VISIBLE
                            }
                        }
                        is MainUiState.Success -> {
                            binding.apply {
                                loading.visibility = View.INVISIBLE
                                textView.visibility = View.VISIBLE
                                textView.text = uiState.text
                            }
                        }
                        is MainUiState.Error -> {
                            binding.apply {
                                loading.visibility = View.INVISIBLE
                                stateFlowTestBtn.visibility = View.VISIBLE
                                callbackFlowTestBtn.visibility = View.VISIBLE
                                textView.visibility = View.VISIBLE
                                textView.text = uiState.throwable.message
                            }
                        }
                    }
                }
            }
        }
    }
}

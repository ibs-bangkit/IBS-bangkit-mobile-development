package com.example.ibscapstone.ui.result

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ibscapstone.R
import com.example.ibscapstone.data.PredictionData
import com.example.ibscapstone.databinding.ActivityResultBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val viewModel: ResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        observeViewModel()

        // Get image path from intent
        intent.getStringExtra(EXTRA_IMAGE_PATH)?.let { path ->
            viewModel.analyzeImage(File(path))
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun observeViewModel() {
        viewModel.predictionState.observe(this) { state ->
            when (state) {
                is ResultState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentGroup.visibility = View.GONE
                }
                is ResultState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentGroup.visibility = View.VISIBLE
                    displayResult(state.data)
                }
                is ResultState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentGroup.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    // Optional: Add a retry button or finish the activity
                    finish()
                }
            }
        }
    }

    private fun displayResult(data: PredictionData) {
        with(binding) {
            textResult.text = data.result
            textExplanation.text = data.explanation
            textSuggestion.text = data.suggestion
            textConfidence.text = getString(R.string.confidence_format, data.confidenceScore)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_IMAGE_PATH = "extra_image_path"
    }
}
package com.example.ibscapstone.ui.result

import android.graphics.BitmapFactory
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
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        observeViewModel()

        // Get image path from intent and display the image
        intent.getStringExtra(EXTRA_IMAGE_PATH)?.let { path ->
            imagePath = path
            displayImage(path)
            viewModel.analyzeImage(File(path))
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun displayImage(path: String) {
        try {
            // Load the image from file and set it to the ImageView
            val bitmap = BitmapFactory.decodeFile(path)
            binding.imageResult.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
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
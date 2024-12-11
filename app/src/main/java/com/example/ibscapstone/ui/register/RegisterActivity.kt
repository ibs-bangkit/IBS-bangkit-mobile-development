package com.example.ibscapstone.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ibscapstone.databinding.ActivityRegisterBinding
import com.example.ibscapstone.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()
        observeState()
    }

    private fun setupActions() {
        binding.btnRegister.setOnClickListener {
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            lifecycleScope.launch {
                viewModel.register(email, password)
            }
        }
        binding.tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun observeState() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is RegisterState.Success -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is RegisterState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                RegisterState.Loading -> {
                    // Show loading indicator
                }
            }
        }
    }
}
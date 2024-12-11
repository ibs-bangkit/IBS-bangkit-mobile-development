package com.example.ibscapstone.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ibscapstone.databinding.ActivityLoginBinding
import com.example.ibscapstone.ui.main.MainActivity
import com.example.ibscapstone.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()
        observeState()

        // Check if already logged in
        checkInitialAuthStatus()
    }

    private fun checkInitialAuthStatus() {
        lifecycleScope.launch {
            viewModel.loginState.observe(this@LoginActivity) { state ->
                if (state == LoginState.Success) {
                    navigateToMainActivity()
                }
            }
        }
    }

    private fun setupActions() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            lifecycleScope.launch {
                viewModel.login(email, password)
            }
        }

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeState() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Success -> {
                    navigateToMainActivity()
                }
                is LoginState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                LoginState.Loading -> {
                    // Show loading indicator
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
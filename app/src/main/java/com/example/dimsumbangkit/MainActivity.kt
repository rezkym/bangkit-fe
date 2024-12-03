package com.example.dimsumbangkit

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dimsumbangkit.databinding.ActivityMainBinding
import com.example.dimsumbangkit.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()
    }

    private fun setupActions() {
        binding.btnLogin.setOnClickListener {
            // Menggunakan path yang benar ke LoginActivity dalam package ui.login
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Optional: tutup MainActivity jika tidak ingin user kembali ke onboarding
        }

        binding.btnSignup.setOnClickListener {
            // Pastikan path Register1Activity sesuai dengan structure folder Anda
            startActivity(Intent(this, Register1Activity::class.java))
            finish() // Optional: tutup MainActivity jika tidak ingin user kembali ke onboarding
        }
    }
}
package com.captchakaro.appy

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.captchakaro.appy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Check if the user has already entered their name
        if (sharedPreferences.getBoolean("isFirstLaunch", false)) {
            navigateToHomeActivity()
            return
        }

        // Inflate the view and setup UI
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle Start Button click
        binding.btnStart.setOnClickListener {
            val name = binding.editTextName.text.toString()
            if (name.isNotEmpty()) {
                saveUserDetails(name)
                navigateToHomeActivity()
            } else {
                binding.editTextName.error = "Please enter your name"
            }
        }
    }

    private fun saveUserDetails(name: String) {
        val editor = sharedPreferences.edit()
        editor.putString("USER_NAME", name)
        editor.putBoolean("isFirstLaunch", true)
        editor.apply()
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Finish MainActivity
    }
}

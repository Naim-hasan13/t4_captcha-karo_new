package com.captchakaro.appy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.captchakaro.appy.databinding.ActivityGivetestBinding
import com.google.android.material.snackbar.Snackbar

class givetestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGivetestBinding
    private var totalEntries = 0
    private var correctCount = 0
    private lateinit var sharedPreferences: SharedPreferences

    private val PREFS_NAME = "BalancePrefs"
    private val BALANCE_KEY = "balanceKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGivetestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        // Retrieve the username from SharedPreferences
        val userName = sharedPreferences.getString("USER_NAME", "Guest")
        // Set the welcome message
        binding.Welcome.text = "Hey,\n$userName!"

        // Load saved balance
        loadBalance()

        // Start timer
        startTimer()

        // Set initial capture code
        generateCaptureCode()

        // Handle the submit button click
        binding.btnSubmit.setOnClickListener {
            checkCaptureCode()
        }

        // Handle the OK button click (for when the time expires)
        binding.ok.setOnClickListener {
            finish() // Close the screen when OK is clicked
        }

        binding.llwalet.setOnClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }
    }

    private fun startTimer() {
        val timerDuration = 60000L // 1 minute in milliseconds
        object : CountDownTimer(timerDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.mainWork.visibility = View.GONE // Hide main content
                binding.done.visibility = View.VISIBLE // Show "done" layout
                binding.pointcount.text = "You Done $correctCount In 60 Seconds"
                binding.lottieAnimationView.playAnimation() // Show the count of correct entries
            }
        }.start()
    }

    private fun generateCaptureCode() {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwx"
        val length = 6 // Set the length of the capture code
        val randomString = (1..length)
            .map { characters.random() }
            .joinToString("")
        binding.tvCaptureCode.text = randomString
    }

    private fun checkCaptureCode() {
        val userInput = binding.etInput.text.toString()
        if (userInput == binding.tvCaptureCode.text.toString()) {
            // Correct input
            correctCount++
            totalEntries++

            // Update balance using BalanceManager
            BalanceManager.addPoints(this, 1)

            // Update UI
            binding.tvBalance.text = BalanceManager.getBalance(this).toString()

            // Save the updated balance to SharedPreferences
            saveBalance(totalEntries)

            // Show success notification
            showNotification(true)

            // Generate a new capture code
            generateCaptureCode()
            binding.etInput.text.clear()
        } else {
            // Incorrect input
            showNotification(false)

            // Generate a new capture code
            generateCaptureCode()
            binding.etInput.text.clear()
        }
    }

    private fun showNotification(isCorrect: Boolean) {
        val message = if (isCorrect) {
            "Your Answer is Correct"
        } else {
            "Your Answer is Incorrect"
        }

        val color = if (isCorrect) {
            resources.getColor(R.color.green)
        } else {
            resources.getColor(R.color.red)
        }

        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(color)
        snackbar.setTextColor(resources.getColor(android.R.color.white))  // Set text color to white

        // Anchor the snackbar to the view that isn't obscured by the keyboard
        snackbar.anchorView = binding.btnSubmit // Replace with your view, e.g., a button or a specific layout
        snackbar.show()
    }

    private fun saveBalance(balance: Int) {
        // Check if a balance has already been saved
        val currentBalance = sharedPreferences.getInt(BALANCE_KEY, -1)

        // Only save the balance if it's not already set (default value of -1 means it's not saved yet)
        if (currentBalance == -1) {
            val editor = sharedPreferences.edit()
            editor.putInt(BALANCE_KEY, balance)
            editor.apply()
        }
    }

    private fun loadBalance() {
        // Load the saved balance from SharedPreferences
        val savedBalance = sharedPreferences.getInt(BALANCE_KEY, 0)
        binding.tvBalance.text = savedBalance.toString() // Set the loaded balance in the UI
    }
}

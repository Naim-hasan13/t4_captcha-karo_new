package com.captchakaro.appy

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.captchakaro.appy.databinding.ActivityWalletBinding

class WalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the username from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "Guest")

        // Set the welcome message
        binding.Welcome.text = "Hey,\n$userName!"
        val currentBalance = BalanceManager.getBalance(this)
        binding.tvBalance.text = currentBalance.toString()
    }

}

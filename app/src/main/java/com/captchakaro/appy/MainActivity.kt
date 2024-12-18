package com.captchakaro.appy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.captchakaro.appy.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Check if the user has already entered their name



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
        MobileAds.initialize(this@MainActivity) {}
        appOpenAdManager = AppOpenAdManager()
        Handler(Looper.getMainLooper()).postDelayed({
            appOpenAdManager.loadAd(this)
        }, 1500)
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

    private inner class AppOpenAdManager {
        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false
        private fun isAdAvailable(): Boolean {
            return appOpenAd != null
        }

        val adUnitId = if (TinyDB.getString(
                this@MainActivity,
                "adx_app_open",
                "/21775744923/example/app-open"
            ) == "/21775744923/example/app-open"
        ) {
            "/21775744923/example/app-open"
        } else {
            TinyDB.getString(this@MainActivity, "adx_app_open", "/21775744923/example/app-open")!!
                .replace("-", ",")
        }

        fun loadAd(context: Context) {
            if (isLoadingAd || isAdAvailable()) {
                return
            }

            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                adUnitId,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        isLoadingAd = false
                        appOpenAdManager.showAdIfAvailable(
                            activity = this@MainActivity,
                            object : OnShowAdCompleteListener {
                                override fun onShowAdComplete() {
                                    if (sharedPreferences.getBoolean("isFirstLaunch", false)) {
                                        navigateToHomeActivity()
                                        finish()
                                    }else{
                                        binding.llLogo.visibility=View.GONE
                                        binding.llName.visibility=View.VISIBLE
                                    }
                                }
                            })
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Called when an app open ad has failed to load.
                        loadAd(context)
                        isLoadingAd = false;
                    }
                })
        }

        fun showAdIfAvailable(
            activity: Activity,
            onShowAdCompleteListener: OnShowAdCompleteListener
        ) {
            if (isShowingAd) {
                return
            }

            if (!isAdAvailable()) {
                onShowAdCompleteListener.onShowAdComplete()

                return
            }

            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()

                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {

                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()

                }

                override fun onAdShowedFullScreenContent() {

                }
            }
            isShowingAd = true
            appOpenAd?.show(activity)
        }
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }
}

package com.captchakaro.appy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.captchakaro.appy.databinding.ActivityWithdrowBinding
import com.captchakaro.appy.extrazz.AdmobX
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback

class WithdrowActivity : AppCompatActivity() {
    lateinit var binding: ActivityWithdrowBinding
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWithdrowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            val insetsController = ViewCompat.getWindowInsetsController(v)
            insetsController?.isAppearanceLightStatusBars = true
            insets
        }
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "Guest")
        binding.tvBalance.text = TinyDB.getString(this, "balance", "0")
        binding.Welcome.text = "Hey,\n$userName!"
        MobileAds.initialize(this@WithdrowActivity) {

        }
        loadInterstitial()
        loadNativeAd()

        binding.ll10.setOnClickListener {
            if (TinyDB.getString(this, "balance", "0")!!.toInt() >= 1000
            ) {
                if (mAdManagerInterstitialAd != null) {
                    mAdManagerInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Reset the ad object and preload a new one
                                mAdManagerInterstitialAd = null
                                val intent =
                                    Intent(this@WithdrowActivity, WalletActivity::class.java)
                                intent.putExtra("coin", "1000")
                                intent.putExtra("title", "Rs. 10")
                                startActivity(intent)
                                loadInterstitial()
                            }

                        }

                    // Show the ad
                    mAdManagerInterstitialAd?.show(this)

                } else {
                    // Notify user and reload ad
                    Toast.makeText(this, "Ads not loaded yet. Please wait.", Toast.LENGTH_SHORT)
                        .show()
                    loadInterstitial()
                }
            } else {
                Toast.makeText(this, "Insufficient Balance", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ll50.setOnClickListener {
            if (TinyDB.getString(this, "balance", "0")!!.toInt() >= 4500) {
                if (mAdManagerInterstitialAd != null) {
                    mAdManagerInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Reset the ad object and preload a new one
                                mAdManagerInterstitialAd = null
                                val intent =
                                    Intent(this@WithdrowActivity, WalletActivity::class.java)
                                intent.putExtra("coin", "4500")
                                intent.putExtra("title", "Rs. 50")
                                startActivity(intent)
                                loadInterstitial()
                            }

                        }

                    // Show the ad
                    mAdManagerInterstitialAd?.show(this)

                } else {
                    // Notify user and reload ad
                    Toast.makeText(this, "Ads not loaded yet. Please wait.", Toast.LENGTH_SHORT)
                        .show()
                    loadInterstitial()
                }
            } else {
                Toast.makeText(this, "Insufficient Balance", Toast.LENGTH_SHORT).show()
            }
        }
        binding.ll100.setOnClickListener {
            if (TinyDB.getString(this, "balance", "0")!!.toInt() >= 80000) {
                if (mAdManagerInterstitialAd != null) {
                    mAdManagerInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Reset the ad object and preload a new one
                                mAdManagerInterstitialAd = null
                                val intent =
                                    Intent(this@WithdrowActivity, WalletActivity::class.java)
                                intent.putExtra("coin", "80000")
                                intent.putExtra("title", "Rs. 100")
                                startActivity(intent)
                                loadInterstitial()
                            }

                        }

                    // Show the ad
                    mAdManagerInterstitialAd?.show(this)

                } else {
                    // Notify user and reload ad
                    Toast.makeText(this, "Ads not loaded yet. Please wait.", Toast.LENGTH_SHORT)
                        .show()
                    loadInterstitial()
                }
            } else {
                Toast.makeText(this, "Insufficient Balance", Toast.LENGTH_SHORT).show()
            }
        }
        binding.ll200.setOnClickListener {
            if (TinyDB.getString(this, "balance", "0")!!.toInt() >= 150000) {
                if (mAdManagerInterstitialAd != null) {
                    mAdManagerInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Reset the ad object and preload a new one
                                mAdManagerInterstitialAd = null
                                val intent =
                                    Intent(this@WithdrowActivity, WalletActivity::class.java)
                                intent.putExtra("coin", "150000")
                                intent.putExtra("title", "Rs. 200")
                                startActivity(intent)
                                loadInterstitial()
                            }

                        }

                    // Show the ad
                    mAdManagerInterstitialAd?.show(this)

                } else {
                    // Notify user and reload ad
                    Toast.makeText(this, "Ads not loaded yet. Please wait.", Toast.LENGTH_SHORT)
                        .show()
                    loadInterstitial()
                }
            } else {
                Toast.makeText(this, "Insufficient Balance", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loadInterstitial() {
        val adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(
            this,
            TinyDB.getString(this@WithdrowActivity, "adx_inter", "")!!.replace("-", ","),
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: com.google.android.gms.ads.LoadAdError) {
                    adError.toString().let { Log.d("AdMob", it) }
                    mAdManagerInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    Log.d("AdMob", "Ad was loaded.")
                    mAdManagerInterstitialAd = interstitialAd
                }
            }
        )
    }

    private fun loadNativeAd() {
        val adLoader = com.google.android.gms.ads.AdLoader.Builder(
            this,
            TinyDB.getString(this@WithdrowActivity, "adx_native", "")!!.replace("-", ",")
        ).forNativeAd { nativeAd ->
            Log.d("AdMob", "Native Ad Loaded.")
            AdmobX.loadNativeMediaumX(
                this,
                binding.myTemplate,
                TinyDB.getString(this@WithdrowActivity, "adx_native", "")!!.replace("-", ",")
            )
        }.build()

        adLoader.loadAd(AdManagerAdRequest.Builder().build())
    }

    override fun onResume() {
        super.onResume()
        binding.tvBalance.text = TinyDB.getString(this, "balance", "0")
    }
}
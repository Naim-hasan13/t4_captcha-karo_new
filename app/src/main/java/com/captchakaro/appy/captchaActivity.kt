package com.captchakaro.appy

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.captchakaro.appy.databinding.ActivityCaptchaBinding
import com.captchakaro.appy.extrazz.AdmobX
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.kabir.moneytree.extrazz.videoplayyer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class captchaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCaptchaBinding
    private lateinit var sharedPreferences: android.content.SharedPreferences
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd?= null
    init {
        System.loadLibrary("keys")
    }
    var isApiCallable = true

    external fun Hatbc(): String
    companion object {
        const val TOTAL_LIMIT_KEY = "TOTAL_LIMIT"
        const val EARNINGS_KEY = "TODAY_EARNINGS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaptchaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInterstitial()
        loadNativeAd()
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Retrieve and display username
        val userName = sharedPreferences.getString("USER_NAME", "Guest")
        binding.Welcome.text = "Hey,\n$userName!"
        displayBalance()

        // Load and display current stats
        val totalLimit = sharedPreferences.getInt(TOTAL_LIMIT_KEY, 0)
        val earnings = sharedPreferences.getInt(EARNINGS_KEY, 0)

        updateUI()

        // Initialize the first captcha
        generateCaptureCode()

        // Navigate to Wallet
        binding.llwalet.setOnClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }

        // Submit button logic
        binding.btnSubmit.setOnClickListener {
            val input = binding.etInput.text.toString()
            val captureCode = binding.tvCaptureCode.text.toString()

            if (TinyDB.getString(this,"play_limit","0").toString().toInt() == 0) {
                // User reached the max limit, show a message and prevent further actions
                showSnackbar("Daily limit reached. Come back tomorrow!")
                return@setOnClickListener
            }

            if (input == captureCode) {
                handleCorrectInput(totalLimit, earnings)
            } else {
                handleIncorrectInput()
            }
        }
    }

    private fun handleCorrectInput(totalLimit: Int, earnings: Int) {
        // Update values
        val newLimit = totalLimit + 1
        val newEarnings = earnings + 1

        // Save updated values to SharedPreferences
        with(sharedPreferences.edit()) {
            putInt(TOTAL_LIMIT_KEY, newLimit)
            putInt(EARNINGS_KEY, newEarnings)
            apply()
        }

        // Add points to wallet using BalanceManager
        BalanceManager.addPoints(this, 1)

        // Update UI with new values
        updateUI()

        // Show success screen temporarily

        binding.mainWork.visibility = android.view.View.GONE
        binding.llRight.visibility = android.view.View.VISIBLE
        var isAdsShowable = TinyDB.getString(this, "play_limit", "0").toString()
            .toInt() % TinyDB.getString(this, "show_ads_after_each", "0").toString()
            .toInt() == 0
        binding.ok.setOnClickListener {
            if (isAdsShowable) {
                if (mAdManagerInterstitialAd != null) {
                    mAdManagerInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Reset the ad object and preload a new one
                                mAdManagerInterstitialAd = null
                                loadInterstitial()

                                // Perform main work after ad dismissal
                                performMainWork()
                                addPoint()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Reset the ad object and proceed with work
                                mAdManagerInterstitialAd = null
                                loadInterstitial()

                                performMainWork()
                                addPoint()
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
                // Ads are not showable; proceed with work
                performMainWork()
                addPoint()
            }
        }
    }


        private fun handleIncorrectInput() {
        // Show failure screen temporarily
        binding.mainWork.visibility = android.view.View.GONE
        binding.llWrong.visibility = android.view.View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.mainWork.visibility = android.view.View.VISIBLE
            binding.llWrong.visibility = android.view.View.GONE
            binding.etInput.text.clear()
            generateCaptureCode()
        }, 2000)
    }

    private fun performMainWork() {
        binding.mainWork.visibility = android.view.View.VISIBLE
        binding.llRight.visibility = android.view.View.GONE
        binding.etInput.text.clear()
        generateCaptureCode()
        displayBalance()
    }

    private fun generateCaptureCode() {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwx"
        val length = 6
        val randomString = (1..length)
            .map { characters.random() }
            .joinToString("")
        binding.tvCaptureCode.text = randomString
    }
    private fun addPoint() {
        Utils.showLoadingPopUp(this)
        if (TinyDB.getString(this, "play_limit", "0") == "0") {
            Utils.dismissLoadingPopUp()
            Toast.makeText(this, "Today's Limit End, Come Back Tomorrow !", Toast.LENGTH_SHORT)
                .show()
            finish()
        } else {
            val deviceid: String = Settings.Secure.getString(
                contentResolver, Settings.Secure.ANDROID_ID
            )
            val time = System.currentTimeMillis()

            val url3 = "${Companions.siteUrl}play_point.php"
            val email = TinyDB.getString(this, "email", "")

            val queue3: RequestQueue = Volley.newRequestQueue(this)
            val stringRequest =
                object : StringRequest(Method.POST, url3, { response ->

                    val yes = Base64.getDecoder().decode(response)
                    val res = String(yes, Charsets.UTF_8)

                    if (res.contains(",")) {
                        Utils.dismissLoadingPopUp()
                        val alldata = res.trim().split(",")
                        TinyDB.saveString(this, "play_limit", alldata[2])
                        TinyDB.saveString(this, "balance", alldata[1])
                        isApiCallable = true
                        Handler(Looper.getMainLooper()).postDelayed({
                            Utils.dismissLoadingPopUp()
                        }, 1000)

                    } else {
                        Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
                    }

                }, { error ->
                    Utils.dismissLoadingPopUp()
                    Toast.makeText(this, "Internet Slow", Toast.LENGTH_SHORT).show()
                    // requireActivity().finish()
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()

                        val dbit32 = videoplayyer.encrypt(deviceid, Hatbc()).toString()
                        val tbit32 = videoplayyer.encrypt(time.toString(), Hatbc()).toString()
                        val email = videoplayyer.encrypt(email.toString(), Hatbc()).toString()

                        val den64 = Base64.getEncoder().encodeToString(dbit32.toByteArray())
                        val ten64 = Base64.getEncoder().encodeToString(tbit32.toByteArray())
                        val email64 = Base64.getEncoder().encodeToString(email.toByteArray())

                        val encodemap: MutableMap<String, String> = HashMap()
                        encodemap["deijvfijvmfhvfvhfbhbchbfybebd"] = den64
                        encodemap["waofhfuisgdtdrefssfgsgsgdhddgd"] = ten64
                        encodemap["fdvbdfbhbrthyjsafewwt5yt5"] = email64

                        val jason = Json.encodeToString(encodemap)

                        val den264 = Base64.getEncoder().encodeToString(jason.toByteArray())

                        val final = URLEncoder.encode(den264, StandardCharsets.UTF_8.toString())

                        params["dase"] = final

                        val encodedAppID = Base64.getEncoder()
                            .encodeToString(
                                Companions.APP_ID.toString().toByteArray()
                            )
                        params["app_id"] = encodedAppID

                        return params
                    }
                }

            queue3.add(stringRequest)
        }


    }





    private fun updateUI() {
        val totalLimit = TinyDB.getString(this,"play_limit","0")
        val MAX_LIMIT = TinyDB.getString(this,"original_play_limit","0")
        val earnings = sharedPreferences.getInt(EARNINGS_KEY, 0)
        binding.tvTotalLimit.text = "Total Limit Left\n${totalLimit}/$MAX_LIMIT"
        binding.tvEarnings.text = "Today Earnings\n${BalanceManager.getDailyEarnings(this)}"
        binding.tvBalance.text = TinyDB.getString(this,"balance","0")
    }

    private fun displayBalance() {
        binding.tvBalance.text = TinyDB.getString(this,"balance","0")
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_red_dark))
        snackbar.setTextColor(resources.getColor(android.R.color.white))
        snackbar.show()
    }

    private fun loadInterstitial() {
        val adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(
            this,
            TinyDB.getString(this@captchaActivity, "adx_inter", "")!!.replace("-", ","),
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
            TinyDB.getString(this@captchaActivity, "adx_native", "")!!.replace("-", ",")
        ).forNativeAd { nativeAd ->
            Log.d("AdMob", "Native Ad Loaded.")
            AdmobX.loadNativeMediaumX(
                this,
                binding.myTemplate,
                TinyDB.getString(this@captchaActivity, "adx_native", "")!!.replace("-", ",")
            )
        }.build()

        adLoader.loadAd(AdManagerAdRequest.Builder().build())
    }

}

package com.captchakaro.appy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.captchakaro.appy.databinding.ActivityGivetestBinding
import com.captchakaro.appy.extrazz.AdmobX
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.kabir.moneytree.extrazz.videoplayyer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class givetestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGivetestBinding
    private var totalEntries = 0
    private var correctCount = 0
    private lateinit var sharedPreferences: SharedPreferences
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd?= null
    init {
        System.loadLibrary("keys")
    }
    var isApiCallable = true

    external fun Hatbc(): String
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
loadNativeAd()
        loadInterstitial()
        displayBalance()

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
            if (mAdManagerInterstitialAd != null) {
                mAdManagerInterstitialAd?.show(this)
                mAdManagerInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("AdMob", "Ad was dismissed.")
                        mAdManagerInterstitialAd = null
                        finish() // Close the screen after the ad is dismissed
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d("AdMob", "Ad failed to show.")
                        finish() // Close the screen if the ad fails to show
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("AdMob", "Ad showed fullscreen content.")
                    }
                }
            } else {
                Log.d("AdMob", "Ad wasn't ready.")
                finish() // Proceed if the ad is not ready
            }
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
            addPoint()
            totalEntries++

            // Update balance using BalanceManager
            BalanceManager.addPoints(this, 1)

            // Update UI
            binding.tvBalance.text = TinyDB.getString(this,"balance","0")


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
        binding.tvBalance.text = TinyDB.getString(this,"balance","0")

    }



    private fun loadInterstitial() {
        val adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(
            this,
            TinyDB.getString(this@givetestActivity, "adx_inter", "")!!.replace("-", ","),
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
    private fun displayBalance() {
        binding.tvBalance.text = TinyDB.getString(this,"balance","0")
    }
    private fun loadNativeAd() {
        val adLoader = com.google.android.gms.ads.AdLoader.Builder(
            this,
            TinyDB.getString(this@givetestActivity, "adx_native", "")!!.replace("-", ",")
        ).forNativeAd { nativeAd ->
            Log.d("AdMob", "Native Ad Loaded.")
            AdmobX.loadNativeMediaumX(
                this,
                binding.myTemplate,
                TinyDB.getString(this@givetestActivity, "adx_native", "")!!.replace("-", ",")
            )
        }.build()

        adLoader.loadAd(AdManagerAdRequest.Builder().build())
    }

}

package com.captchakaro.appy

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.captchakaro.appy.databinding.ActivityWalletBinding
import com.captchakaro.appy.extrazz.AdmobX
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.kabir.moneytree.extrazz.videoplayyer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class WalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding

    init {
        System.loadLibrary("keys")
    }

    external fun Hatbc(): String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the username from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "Guest")

        // Set the welcome message
        binding.Welcome.text = "Hey,\n$userName!"
        binding.tvBalance.text = TinyDB.getString(this, "balance", "0")
        binding.btnSubmit.setOnClickListener {
            val upi = binding.etUpi.text.toString()
            if (upi.isEmpty()) {
                redeemPoint(upi)
            }else{
                binding.etUpi.error = "Enter UPI"
            }
        }
        MobileAds.initialize(this) {}
        loadNativeAd()
        binding.etAmount.setText(intent.getStringExtra("title").toString())
    }
    private fun loadNativeAd() {
        val adLoader = com.google.android.gms.ads.AdLoader.Builder(
            this,
            TinyDB.getString(this@WalletActivity, "adx_native", "")!!.replace("-", ",")
        ).forNativeAd { nativeAd ->
            Log.d("AdMob", "Native Ad Loaded.")
            AdmobX.loadNativeMediaumX(
                this,
                binding.myTemplate,
                TinyDB.getString(this@WalletActivity, "adx_native", "")!!.replace("-", ",")
            )
        }.build()

        adLoader.loadAd(AdManagerAdRequest.Builder().build())
    }
    private fun redeemPoint(upi: String) {
        Utils.showLoadingPopUp(this)
        if (upi.isEmpty()) {
            Toast.makeText(this, "Enter UPI", Toast.LENGTH_SHORT).show()
            return
        }

        val deviceid: String = Settings.Secure.getString(
            this.contentResolver, Settings.Secure.ANDROID_ID
        )
        val time = System.currentTimeMillis()

        val url3 = "${Companions.siteUrl}redeem_point_2.php"
        val email = TinyDB.getString(this, "email", "")

        val queue3: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, url3, { response ->

            val yes = Base64.getDecoder().decode(response)
            val res = String(yes, Charsets.UTF_8)

            if (res.contains(",")) {
                Utils.dismissLoadingPopUp()
                val alldata = res.trim().split(",")

                TinyDB.saveString(this, "balance", alldata[1])
                Toast.makeText(this, alldata[0], Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    Utils.dismissLoadingPopUp()

                    finish()

                }, 1000)

            } else {
                Toast.makeText(this, res, Toast.LENGTH_LONG).show()
                finish()
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
                val upi32 = videoplayyer.encrypt(upi.toString(), Hatbc()).toString()
                val coin32 = videoplayyer.encrypt(intent.getStringExtra("coin").toString(), Hatbc())
                    .toString()
                val title32 =
                    videoplayyer.encrypt(intent.getStringExtra("title").toString(), Hatbc())
                        .toString()

                val den64 = Base64.getEncoder().encodeToString(dbit32.toByteArray())
                val ten64 = Base64.getEncoder().encodeToString(tbit32.toByteArray())
                val email64 = Base64.getEncoder().encodeToString(email.toByteArray())
                val upi64 = Base64.getEncoder().encodeToString(upi32.toByteArray())
                val coin64 = Base64.getEncoder().encodeToString(coin32.toByteArray())
                val title64 = Base64.getEncoder().encodeToString(title32.toByteArray())

                val encodemap: MutableMap<String, String> = HashMap()
                encodemap["deijvfijvmfhvfvhfbhbchbfybebd"] = den64
                encodemap["waofhfuisgdtdrefssfgsgsgdhddgd"] = ten64
                encodemap["fdvbdfbhbrthyjsafewwt5yt5"] = email64
                encodemap["defsdfefsefwefwefewfwefvfvdfbdbd"] = upi64
                encodemap["balsdfefsefwefwefewfwefvfvdfbdbd"] = coin64
                encodemap["namsdfefsefwefwefewfwefvfvdfbdbd"] = title64

                val jason = Json.encodeToString(encodemap)

                val den264 = Base64.getEncoder().encodeToString(jason.toByteArray())

                val final = URLEncoder.encode(den264, StandardCharsets.UTF_8.toString())

                params["dase"] = final

                val encodedAppID = Base64.getEncoder().encodeToString(
                    Companions.APP_ID.toString().toByteArray()
                )
                params["app_id"] = encodedAppID

                return params
            }
        }

        queue3.add(stringRequest)


    }
}

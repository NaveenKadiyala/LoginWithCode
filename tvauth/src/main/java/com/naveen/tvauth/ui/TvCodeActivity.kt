package com.naveen.tvauth.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.naveen.tvauth.data.FirestoreHelper
import com.naveen.tvauth.data.FirestoreHelper.CODE_EXPIRY_IN_MIN
import com.naveen.tvauth.data.SharedPrefHelper
import com.naveen.tvauth.databinding.ActivityTvCodeBinding
import com.naveen.tvauth.shortToast

class TvCodeActivity : FragmentActivity() {

    private lateinit var binding: ActivityTvCodeBinding
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (SharedPrefHelper.getInstance(this).getUserId().isEmpty()) {
            // Not Logged In
            createAndStoreActivationCode()
        } else {
            // Logged In
            openUserDetailsScreen()
        }
    }

    private fun createAndStoreActivationCode() {
        val activationCode = getRandomString()
        binding.codeTxtView.text = activationCode
        generateQrCodeAndDisplay(activationCode)
        FirestoreHelper.saveTvCode(this, activationCode) { userId, error ->
            if (error != null) {
                shortToast(error)
            } else {
                SharedPrefHelper.getInstance(this@TvCodeActivity).storeUserId(userId!!)
                FirestoreHelper.addDeviceInUserDevicesList(userId)
                openUserDetailsScreen()
            }
        }
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed({
            createAndStoreActivationCode()
        }, CODE_EXPIRY_IN_MIN * 60 * 1000)
    }

    private fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..6)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun openUserDetailsScreen() {
        startActivity(
            Intent(this, UserDetailsActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
        )
    }

    private fun generateQrCodeAndDisplay(content: String) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 600, 600)
            binding.qrCodeImgView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        handler?.removeCallbacksAndMessages(null)
    }
}
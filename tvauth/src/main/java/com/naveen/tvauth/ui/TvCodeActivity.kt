package com.naveen.tvauth.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.naveen.tvauth.TvAuthApp
import com.naveen.tvauth.data.FirestoreHelper
import com.naveen.tvauth.data.SharedPrefHelper
import com.naveen.tvauth.databinding.ActivityTvCodeBinding
import com.naveen.tvauth.shortToast

class TvCodeActivity : FragmentActivity() {

    private lateinit var binding: ActivityTvCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (SharedPrefHelper.getInstance(this).getUserId().isEmpty()) {
            // Not Logged In
            getFcmToken()
            //  listenForDeviceLogin()
        } else {
            // Logged In
            openUserDetailsScreen()
        }
    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            createAndStoreActivationCode(token)
        })
    }

    private fun createAndStoreActivationCode(token: String) {
        val activationCode = getRandomString()
        binding.codeTxtView.text = activationCode
        FirestoreHelper.saveTvCode(this, token, activationCode) { userId, error ->
            if (error != null) {
                shortToast(error)
            } else {
                SharedPrefHelper.getInstance(this@TvCodeActivity).storeUserId(userId!!)
                openUserDetailsScreen()
            }
        }
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

    private fun listenForDeviceLogin() {
        TvAuthApp.getInstance().firebaseUserIdLiveData.observe(this, {
            Log.d("TAG", "listenForDeviceLogin: $it")
            SharedPrefHelper.getInstance(this).storeUserId(it)
            openUserDetailsScreen()
        })
    }
}
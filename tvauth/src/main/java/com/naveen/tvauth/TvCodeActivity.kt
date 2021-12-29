package com.naveen.tvauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.naveen.tvauth.databinding.ActivityTvCodeBinding

class TvCodeActivity : FragmentActivity() {

    private lateinit var binding: ActivityTvCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (SharedPrefHelper.getInstance(this).getUserId().isEmpty()) {
            // Not Logged In
            getToken()
            listenForDeviceLogin()
        } else {
            // Logged In
            openUserDetailsScreen()
        }
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
        TvAuthApp.getInstance()._firebaseUserId.observe(this, {
            Log.d("TAG", "listenForDeviceLogin: $it")
            SharedPrefHelper.getInstance(this).storeUserId(it)
            openUserDetailsScreen()
        })
    }

    private fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..6)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("TAG", "getToken: $token")
            val generatedCode = getRandomString()
            binding.codeTxtView.text = generatedCode
            FirestoreHelper.saveTvCode(token, generatedCode)
        })
    }
}
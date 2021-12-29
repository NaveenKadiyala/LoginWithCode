package com.naveen.mobileauth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.naveen.mobileauth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if (currentUser != null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragContainer.id, TvCodeFragment.newInstance())
                .addToBackStack("")
                .commitAllowingStateLoss()
        } else
            supportFragmentManager.beginTransaction()
                .replace(binding.fragContainer.id, LoginFragment.newInstance())
                .addToBackStack("")
                .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}
package com.naveen.tvauth.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.naveen.tvauth.R
import com.naveen.tvauth.data.FirestoreHelper
import com.naveen.tvauth.data.SharedPrefHelper
import com.naveen.tvauth.databinding.ActivityUserDetailsBinding
import com.naveen.tvauth.shortToast

class UserDetailsActivity : FragmentActivity() {

    private lateinit var binding: ActivityUserDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoutBtn.setOnClickListener {
            FirestoreHelper.clearUserId(SharedPrefHelper.getInstance(this).getUserId())
            SharedPrefHelper.getInstance(this).storeUserId("")
            startActivity(
                Intent(this, TvCodeActivity::class.java)
                    .apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
            )
        }
        getUserFromFirestore()
    }

    private fun getUserFromFirestore() {
        FirestoreHelper.getUserDetails(
            SharedPrefHelper.getInstance(this).getUserId()
        ) { user, error ->
            if (error != null) shortToast(error)
            else binding.mailTv.text = getString(R.string.mail, user?.mail)
        }
    }
}
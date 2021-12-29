package com.naveen.tvauth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.naveen.tvauth.databinding.ActivityUserDetailsBinding

class UserDetailsActivity : FragmentActivity() {

    private lateinit var binding: ActivityUserDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoutBtn.setOnClickListener {
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
        FirestoreHelper.getUserDetails(SharedPrefHelper.getInstance(this).getUserId(),
            object : FirestoreHelper.FireStoreHelperInterface {
                override fun onSuccess(user: FirestoreHelper.User) {
                    binding.mailTv.text = getString(R.string.mail, user.mail)
                }

                override fun onFailure(msg: String) {
                    Toast.makeText(this@UserDetailsActivity, msg, Toast.LENGTH_SHORT).show()
                }

            })
    }
}
package com.naveen.mobileauth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.naveen.mobileauth.databinding.FragmentTvCodeBinding
import com.naveen.mobileauth.remote.ApiHelper


class TvCodeFragment : Fragment() {

    private lateinit var binding: FragmentTvCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTvCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TvCodeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.verifyCodeBtn.setOnClickListener {
            val inputCode = binding.codeEt.text.toString()
            FirestoreHelper.verifyCode(inputCode, object : FirestoreHelper.FireStoreInterface {
                override fun onSuccess(fcmToken: String) {
                    Toast.makeText(requireContext(), "Tv Login Successful", Toast.LENGTH_SHORT).show()
                    /*ApiHelper.sendPushNotification(fcmToken, Firebase.auth.currentUser?.uid!!,
                    object : ApiHelper.ApiResponseListener{
                        override fun onSuccess() {
                            Toast.makeText(requireContext(), "Tv Login Successful", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(msg: String) {
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        }

                    })*/
                }

                override fun onFailure(msg: String) {
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }

            })
        }

        binding.logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(requireActivity(), MainActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
        }
    }
}
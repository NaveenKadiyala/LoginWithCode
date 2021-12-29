package com.naveen.mobileauth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.naveen.mobileauth.*
import com.naveen.mobileauth.data.FirebaseAuthHelper
import com.naveen.mobileauth.databinding.FragmentLoginBinding
import com.naveen.mobileauth.replaceFragment

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToRegBtn.setOnClickListener {
            requireActivity().supportFragmentManager
                .replaceFragment(R.id.frag_container, RegisterFragment.newInstance())
        }

        binding.logBtn.setOnClickListener {
            val email = binding.logEmailEt.text.toString()
            val pass = binding.logPassEt.text.toString()
            FirebaseAuthHelper.loginWithEmailPass(email, pass) { _, error ->
                if (error != null) {
                    requireActivity().shortToast(error)
                } else {
                    requireActivity().shortToast("Login successful")
                    requireActivity().supportFragmentManager
                        .replaceFragment(R.id.frag_container, TvCodeFragment.newInstance())
                }
            }
        }
    }


}
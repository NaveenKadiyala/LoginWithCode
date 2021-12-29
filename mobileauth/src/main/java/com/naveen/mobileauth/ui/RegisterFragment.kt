package com.naveen.mobileauth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.naveen.mobileauth.R
import com.naveen.mobileauth.data.FirebaseAuthHelper
import com.naveen.mobileauth.data.FirestoreHelper
import com.naveen.mobileauth.databinding.FragmentRegisterBinding
import com.naveen.mobileauth.replaceFragment
import com.naveen.mobileauth.shortToast

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.regBtn.setOnClickListener {
            val email = binding.regEmailEt.text.toString()
            val pass = binding.regPassEt.text.toString()
            FirebaseAuthHelper.createUser(email, pass) { user, error ->
                if (error != null) {
                    requireActivity().shortToast(error)
                } else {
                    user?.uid?.let { it1 -> FirestoreHelper.saveUser(email, it1) }
                    requireActivity().shortToast("Registration successful")
                    requireActivity().supportFragmentManager
                        .replaceFragment(R.id.frag_container, TvCodeFragment.newInstance())
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }
}
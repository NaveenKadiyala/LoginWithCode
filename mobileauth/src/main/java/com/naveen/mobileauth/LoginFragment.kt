package com.naveen.mobileauth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser
import com.naveen.mobileauth.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

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
        fun newInstance() =
            LoginFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToRegBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frag_container, RegisterFragment.newInstance())
                .addToBackStack("")
                .commitAllowingStateLoss()
        }

        binding.logBtn.setOnClickListener {
            val email = binding.logEmailEt.text.toString()
            val pass = binding.logPassEt.text.toString()
            FirebaseAuhHelper.loginWithEmailPass(
                email,
                pass,
                object : FirebaseAuhHelper.FirebaseHelperInterface {
                    override fun onSuccess(currentUser: FirebaseUser?) {
                        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT)
                            .show()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.frag_container, TvCodeFragment.newInstance())
                            .addToBackStack("")
                            .commitAllowingStateLoss()
                    }

                    override fun onFailure(msg: String) {
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
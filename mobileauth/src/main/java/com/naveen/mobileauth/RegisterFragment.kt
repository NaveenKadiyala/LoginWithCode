package com.naveen.mobileauth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.naveen.mobileauth.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

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
            FirebaseAuhHelper.createUser(
                email,
                pass,
                object : FirebaseAuhHelper.FirebaseHelperInterface {
                    override fun onSuccess(currentUser: FirebaseUser?) {
                        currentUser?.uid?.let { it1 -> FirestoreHelper.saveUser(email, it1) }
                        Toast.makeText(
                            requireContext(),
                            "Registration successful",
                            Toast.LENGTH_SHORT
                        ).show()
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

    companion object {

        @JvmStatic
        fun newInstance() =
            RegisterFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
package com.naveen.mobileauth.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.naveen.mobileauth.data.FirestoreHelper
import com.naveen.mobileauth.databinding.FragmentTvCodeBinding
import com.naveen.mobileauth.shortToast

class TvCodeFragment : Fragment() {

    private lateinit var binding: FragmentTvCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTvCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = TvCodeFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.verifyCodeBtn.setOnClickListener {
            val inputCode = binding.codeEt.text.toString()
            validateInputCode(inputCode)
        }

        binding.logoutBtn.setOnClickListener {
            FirestoreHelper.clearIsLoggedIn(Firebase.auth.currentUser?.uid!!)
            Firebase.auth.signOut()
            startActivity(Intent(requireActivity(), MainActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
        }

        binding.scanCodeBtn.setOnClickListener {
            val options = ScanOptions()
            options.captureActivity = AnyOrientationCaptureActivity::class.java
            // options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            options.setPrompt("Scan a barcode")
            qrCodeLauncher.launch(options)
        }

    }

    private fun validateInputCode(inputCode: String) {
        if (inputCode.isEmpty()) {
            return
        }
        FirestoreHelper.findCodeDocumentAndVerify(inputCode) { _, error ->
            if (error != null) {
                requireActivity().shortToast(error)
            } else {
                requireActivity().shortToast("Tv Login Successful")
            }
        }
    }

    // Register the launcher and result handler
    private val qrCodeLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            if (result.contents == null) {
                requireActivity().shortToast("Scanning Cancelled")
            } else {
                Log.d("TAG", "QRCode scanned: ${result.contents}")
                validateInputCode(result.contents)
            }
        }
}
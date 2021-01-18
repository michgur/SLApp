package com.klmn.slapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.telephony.TelephonyManager
import android.view.View.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.klmn.slapp.R
import com.klmn.slapp.common.COUNTRY_CODES
import com.klmn.slapp.common.hideKeyboard
import com.klmn.slapp.data.datastore.UserPreferences
import com.klmn.slapp.databinding.ActivityPhoneAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class PhoneAuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityPhoneAuthBinding
    private lateinit var countryCode: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var verificationId: String
    private var verificationInProgress = false

    @Inject lateinit var userPreferences: UserPreferences

    companion object { const val FLAG_VERIFY_NUMBER = "phoneNumberVerify" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.auth.setLanguageCode(resources.configuration.locale.language)

        val manager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        countryCode = "+" + COUNTRY_CODES[manager.simCountryIso.toUpperCase(Locale.ROOT)].toString()

        binding.fieldPhone.prefixText = countryCode
        binding.fieldPhone.editText?.doAfterTextChanged {
            binding.btnSendCode.isEnabled = !it.isNullOrBlank()
        }
        binding.btnSendCode.setOnClickListener {
            hideKeyboard()
            verifyPhoneNumber()
        }
        binding.fieldPhone.editText?.doAfterTextChanged {
            binding.btnSendCode.isEnabled = true

            binding.btnSignIn.visibility = GONE
            binding.fieldCode.visibility = GONE
            binding.btnResendCode.visibility = GONE
        }
        binding.fieldCode.editText?.doAfterTextChanged {
            binding.btnSignIn.isEnabled = !it.isNullOrBlank()
        }
        binding.btnResendCode.setOnClickListener {
            verifyPhoneNumber(resendToken)
        }
        binding.btnSignIn.setOnClickListener {
            binding.fieldCode.editText?.text?.let {
                signInWithCredential(PhoneAuthProvider.getCredential(verificationId, it.toString()))
            }
        }
    }

    private fun finishWithUser(user: FirebaseUser) = lifecycleScope.launch {
        user.phoneNumber?.let { userPreferences.savePhoneNumber(it) }
        setResult(RESULT_OK, Intent().putExtra("success", user.phoneNumber != null))
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (verificationInProgress) verifyPhoneNumber()
    }

    private fun verifyPhoneNumber(token: PhoneAuthProvider.ForceResendingToken? = null) {
        val phoneNumber = "$countryCode${binding.fieldPhone.editText?.text}"
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
        token?.let(options::setForceResendingToken)
        PhoneAuthProvider.verifyPhoneNumber(options.build())
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    task.result?.user?.let(::finishWithUser)
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException)
                        binding.fieldCode.error = getString(R.string.error_invalid_code)
                    Toast.makeText(
                        this@PhoneAuthActivity,
                        getString(R.string.error_verification_failed),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) =
            signInWithCredential(credential)

        override fun onVerificationFailed(e: FirebaseException) {
            verificationInProgress = false
            if (e is FirebaseAuthInvalidCredentialsException)
                binding.fieldPhone.error = getString(R.string.error_invalid_number)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, token)

            resendToken = token
            this@PhoneAuthActivity.verificationId = verificationId

            binding.btnSignIn.visibility = VISIBLE
            binding.fieldCode.visibility = VISIBLE
            binding.btnResendCode.visibility = VISIBLE
            binding.btnSendCode.isEnabled = false
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        verificationInProgress = savedInstanceState.getBoolean(FLAG_VERIFY_NUMBER)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean(FLAG_VERIFY_NUMBER, verificationInProgress)
    }
}
package com.example.ludostealth.chat

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import com.google.firebase.FirebaseException
class PhoneAuthManager(
    private val activity: Activity
) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var verificationId: String? = null

    // ===============================
    // 🔹 Send OTP
    // ===============================
    fun sendOtp(
        phoneNumber: String,
        onCodeSent: () -> Unit,
        onVerificationSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // +92300...
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // 🔥 Auto verification (OTP auto detect)
                    signInWithCredential(credential, onVerificationSuccess, onError)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("OTP_ERROR", e.message ?: "Error")
                    onError(e.message ?: "Verification failed")
                }

                override fun onCodeSent(
                    verId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verId, token)

                    verificationId = verId
                    onCodeSent()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // ===============================
    // 🔹 Verify OTP manually
    // ===============================
    fun verifyOtp(
        code: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val verId = verificationId ?: return

        val credential = PhoneAuthProvider.getCredential(verId, code)
        signInWithCredential(credential, onSuccess, onError)
    }

    // ===============================
    // 🔹 Sign in
    // ===============================
    private fun signInWithCredential(
        credential: PhoneAuthCredential,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Login failed")
                }
            }
    }
}
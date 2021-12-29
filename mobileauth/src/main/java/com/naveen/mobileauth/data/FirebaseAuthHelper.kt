package com.naveen.mobileauth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthHelper {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginWithEmailPass(email: String, pass: String, callback: (user: FirebaseUser?, error: String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    callback.invoke(currentUser, null)
                } else {
                    task.exception?.printStackTrace()
                    callback.invoke(null, task.exception?.localizedMessage!!)
                }
            }
    }

    fun createUser(email: String, pass: String,  callback: (user: FirebaseUser?, error: String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    callback.invoke(currentUser, null)
                } else {
                    task.exception?.printStackTrace()
                    callback.invoke(null, task.exception?.localizedMessage!!)
                }
            }
    }
}
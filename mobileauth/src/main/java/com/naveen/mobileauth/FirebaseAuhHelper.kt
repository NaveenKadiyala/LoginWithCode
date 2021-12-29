package com.naveen.mobileauth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuhHelper {

    interface FirebaseHelperInterface {
        fun onSuccess(currentUser: FirebaseUser?)
        fun onFailure(msg: String)
    }

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginWithEmailPass(email: String, pass: String, callback: FirebaseHelperInterface) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    callback.onSuccess(currentUser)
                } else {
                    task.exception?.printStackTrace()
                    callback.onFailure(task.exception?.localizedMessage!!)
                }
            }
    }

    fun createUser(email: String, pass: String, callback: FirebaseHelperInterface) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    callback.onSuccess(user)
                } else {
                    task.exception?.printStackTrace()
                    callback.onFailure(task.exception?.localizedMessage!!)
                }
            }
    }
}
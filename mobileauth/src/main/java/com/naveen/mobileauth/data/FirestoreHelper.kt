package com.naveen.mobileauth.data

import android.os.Build
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.ktx.Firebase
import com.naveen.mobileauth.MobileAuthApp
import java.util.*

object FirestoreHelper {

    private const val DEVICE_COLLECTION = "TvDevices"
    private const val USERS_COLLECTION = "Users"
    private const val ACTIVATION_CODE = "activationCode"
    private const val CREATED_DATE = "createdDate"
    private const val ID = "id"
    private const val MAIL = "mail"
    private const val GEN_DATE = "generatedDate"
    private const val USER_ID = "userId"
    private const val DEVICES = "Devices"
    private const val IS_LOGGED_IN = "isLoggedIn"
    private const val CODE_EXPIRY_IN_MIN = 3

    private val db by lazy { FirebaseFirestore.getInstance() }

    fun saveUser(email: String, userId: String) {
        val docRef = userId.let { db.collection(USERS_COLLECTION).document(it) }
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = hashMapOf(
                        ID to userId,
                        MAIL to email,
                        CREATED_DATE to Date()
                    )
                    docRef.set(user)
                    docRef.collection(DEVICES).document()
                        .set(
                            ActiveDevice(
                                deviceName = MobileAuthApp.getInstance().getDeviceId(),
                                isLoggedIn = true
                            )
                        )
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun updateActiveDeviceInUser(userId: String) {
        val docRef = db.collection(USERS_COLLECTION).document(userId)
            .collection(DEVICES).document(MobileAuthApp.getInstance().getDeviceId())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    docRef.set(
                        ActiveDevice(
                            deviceName = MobileAuthApp.getInstance().getDeviceId(),
                            isLoggedIn = true
                        )
                    )
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun clearIsLoggedIn(userId: String) {
        val docRef = db.collection(USERS_COLLECTION).document(userId)
            .collection(DEVICES).document(MobileAuthApp.getInstance().getDeviceId())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    docRef.update(mapOf(IS_LOGGED_IN to false))
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun findCodeDocumentAndVerify(
        code: String,
        callback: (fcmToken: String?, error: String?) -> Unit
    ) {
        val docRef = db.collection(DEVICE_COLLECTION).whereEqualTo(ACTIVATION_CODE, code)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if (document.documents.isEmpty()) {
                        callback.invoke(null, "Wrong Activation Code")
                    } else {
                        Log.d("TAG", "verifyCode: ${document.documents.first()}")
                        verifyActivationCode(document, callback)
                    }
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                callback.invoke(null, it.localizedMessage!!)
            }
    }

    private fun verifyActivationCode(
        document: QuerySnapshot,
        callback: (fcmToken: String?, error: String?) -> Unit
    ) {
        val generatedTime = document.documents.first().getTimestamp(GEN_DATE)?.toDate()
        val currentDate = Date()
        val diffInMillis = currentDate.time - generatedTime?.time!!
        if (diffInMillis > CODE_EXPIRY_IN_MIN * 60 * 1000) {
            callback.invoke(null, "Activation Code expired, Please refresh Tv screen once")
        } else {
            val docId = document.documents.first().id
            db.collection(DEVICE_COLLECTION).document(docId)
                .update(mapOf(USER_ID to Firebase.auth.currentUser?.uid!!))
                .addOnSuccessListener {
                    callback.invoke(docId, null)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback.invoke(null, it.localizedMessage!!)
                }
        }
    }

    data class ActiveDevice(
        val deviceName: String = Build.DEVICE,
        val loggedInTime: Date = Date(),
        val isLoggedIn: Boolean = false
    )
}
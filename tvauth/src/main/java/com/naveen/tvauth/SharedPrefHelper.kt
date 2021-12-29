package com.naveen.tvauth

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
    }

    private val editor by lazy { sharedPreferences.edit() }

    companion object : SingletonHolder<SharedPrefHelper, Context>(::SharedPrefHelper) {
        private const val PREF_NAME = "USER_PREF"
        private const val USER_ID = "user_id"
    }

    fun storeUserId(userId: String) {
        editor.apply {
            putString(USER_ID, userId)
            apply()
        }
    }

    fun getUserId(): String {
        return sharedPreferences.getString(USER_ID, "") ?: ""
    }
}
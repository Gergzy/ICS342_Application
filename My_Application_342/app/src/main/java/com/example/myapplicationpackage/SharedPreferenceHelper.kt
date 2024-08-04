package com.example.myapplicationpackage


import android.content.Context
import android.content.SharedPreferences

interface PreferencesHelper {
    fun saveUserID(userID:String)
    fun getUserID(): String?
    fun saveUserToken(token:String)
    fun getUserToken(): String?
    fun saveToDoID(todoID:String)
    fun getToDoID(): String?
}

class SharedPreferencesHelper(context: Context): PreferencesHelper {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREFS_NAME = "user_prefs"
    }


    override fun saveUserID(userID: String) {
        sharedPreferences.edit().putString("userID", userID).apply()
    }

    override fun getUserID(): String? {
        return sharedPreferences.getString("userID", "")
    }

    override fun saveUserToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    override fun getUserToken(): String? {
        return sharedPreferences.getString("token", "")
    }

    override fun saveToDoID(todoID: String) {
        sharedPreferences.edit().putString("todoID", todoID).apply()
    }

    override fun getToDoID(): String? {
        return sharedPreferences.getString("todoID", null)
    }
}

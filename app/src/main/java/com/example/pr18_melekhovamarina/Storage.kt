package com.example.pr18_melekhovamarina

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.security.MessageDigest

class Storage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(user: User) {
        prefs.edit().putString(KEY_USER, gson.toJson(user)).apply()
    }

    fun getUser(): User? {
        val json = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun setLoggedIn(value: Boolean) {
        prefs.edit().putBoolean(KEY_LOGGED_IN, value).apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun getDiscs(): MutableList<Disc> {
        val json = prefs.getString(KEY_DISCS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Disc>>() {}.type
        return try {
            gson.fromJson<MutableList<Disc>>(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    private fun saveDiscs(list: List<Disc>) {
        prefs.edit().putString(KEY_DISCS, gson.toJson(list)).apply()
    }

    fun addDisc(disc: Disc) {
        val list = getDiscs()
        list.add(disc)
        saveDiscs(list)
    }

    fun removeDisc(id: Long) {
        val list = getDiscs()
        list.removeAll { it.id == id }
        saveDiscs(list)
    }

    fun clearDiscs() {
        prefs.edit().remove(KEY_DISCS).apply()
    }

    companion object {
        private const val PREFS_FILE = "pr18_prefs"
        private const val KEY_USER = "user"
        private const val KEY_DISCS = "discs"
        private const val KEY_LOGGED_IN = "logged_in"

        fun hash(password: String): String =
            MessageDigest.getInstance("SHA-256")
                .digest(password.toByteArray())
                .joinToString("") { "%02x".format(it) }
    }
}
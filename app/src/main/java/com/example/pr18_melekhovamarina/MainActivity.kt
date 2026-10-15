package com.example.pr18_melekhovamarina

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(),
    AuthFragment.Callbacks,
    AddDiscFragment.Callbacks,
    DiscListFragment.Callbacks {

    private lateinit var storage: Storage
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        storage = Storage(this)
        bottomNav = findViewById(R.id.bottom_nav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_list -> showFragment(DiscListFragment())
                R.id.nav_add -> showFragment(AddDiscFragment())
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        val loggedIn = storage.isLoggedIn() && storage.getUser() != null
        bottomNav.visibility = if (loggedIn) View.VISIBLE else View.GONE

        if (savedInstanceState == null) {
            if (loggedIn) showMain() else showAuth()
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showAuth() {
        bottomNav.visibility = View.GONE
        showFragment(AuthFragment())
    }

    private fun showMain() {
        bottomNav.visibility = View.VISIBLE
        bottomNav.menu.findItem(R.id.nav_list).isChecked = true
        showFragment(DiscListFragment())
    }

    override fun onAuthSuccess() = showMain()

    override fun onDiscSaved() {
        bottomNav.selectedItemId = R.id.nav_list
    }

    override fun onLogout() {
        storage.setLoggedIn(false)
        showAuth()
    }
}
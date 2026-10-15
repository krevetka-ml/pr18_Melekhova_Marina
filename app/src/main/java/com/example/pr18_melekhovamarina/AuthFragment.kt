package com.example.pr18_melekhovamarina

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class AuthFragment : Fragment() {

    interface Callbacks {
        fun onAuthSuccess()
    }

    private var callbacks: Callbacks? = null
    private lateinit var storage: Storage

    private var isRegister = true

    private lateinit var tvTitle: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var tilName: TextInputLayout
    private lateinit var tilLogin: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var btnAction: MaterialButton
    private lateinit var tvToggle: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as? Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_auth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = Storage(requireContext())

        tvTitle = view.findViewById(R.id.tv_title)
        tvSubtitle = view.findViewById(R.id.tv_subtitle)
        tilName = view.findViewById(R.id.til_name)
        tilLogin = view.findViewById(R.id.til_login)
        tilPassword = view.findViewById(R.id.til_password)
        btnAction = view.findViewById(R.id.btn_action)
        tvToggle = view.findViewById(R.id.tv_toggle)

        val hasUser = storage.getUser() != null
        isRegister = savedInstanceState?.getBoolean(KEY_MODE) ?: !hasUser
        updateMode(hasUser)

        btnAction.setOnClickListener {
            if (isRegister) register() else login()
        }
        tvToggle.setOnClickListener {
            isRegister = !isRegister
            clearErrors()
            updateMode(hasUser)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_MODE, isRegister)
    }

    private fun updateMode(hasUser: Boolean) {
        if (isRegister) {
            tvTitle.text = "Добро пожаловать!"
            tvSubtitle.text = "Создайте аккаунт, чтобы собрать свой плей-лист дисков"
            tilName.visibility = View.VISIBLE
            btnAction.text = "Зарегистрироваться"
            tvToggle.text = "Уже есть аккаунт? Войти"
        } else {
            tvTitle.text = "С возвращением!"
            tvSubtitle.text = "Войдите, чтобы открыть свой плей-лист"
            tilName.visibility = View.GONE
            btnAction.text = "Войти"
            tvToggle.text = "Зарегистрировать нового пользователя"
        }
        tvToggle.visibility = if (hasUser) View.VISIBLE else View.GONE
    }

    private fun clearErrors() {
        tilName.error = null
        tilLogin.error = null
        tilPassword.error = null
    }

    private fun TextInputLayout.value(): String =
        editText?.text?.toString()?.trim().orEmpty()

    private fun validate(checkName: Boolean): Boolean {
        clearErrors()
        var ok = true
        if (checkName && tilName.value().isEmpty()) {
            tilName.error = "Введите имя"
            ok = false
        }
        if (tilLogin.value().isEmpty()) {
            tilLogin.error = "Введите логин"
            ok = false
        }
        if (tilPassword.value().isEmpty()) {
            tilPassword.error = "Введите пароль"
            ok = false
        } else if (checkName && tilPassword.value().length < 4) {
            tilPassword.error = "Пароль – не менее 4 символов"
            ok = false
        }
        return ok
    }

    private fun register() {
        if (!validate(checkName = true)) return

        val user = User(
            name = tilName.value(),
            login = tilLogin.value(),
            passwordHash = Storage.hash(tilPassword.value())
        )
        storage.clearDiscs()
        storage.saveUser(user)
        storage.setLoggedIn(true)
        callbacks?.onAuthSuccess()
    }

    private fun login() {
        if (!validate(checkName = false)) return

        val saved = storage.getUser()
        if (saved == null ||
            saved.login != tilLogin.value() ||
            saved.passwordHash != Storage.hash(tilPassword.value())
        ) {
            tilPassword.error = "Неверный логин или пароль"
            return
        }
        storage.setLoggedIn(true)
        callbacks?.onAuthSuccess()
    }

    companion object {
        private const val KEY_MODE = "mode_register"
    }
}
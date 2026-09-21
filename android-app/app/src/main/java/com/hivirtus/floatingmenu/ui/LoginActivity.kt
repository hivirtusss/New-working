package com.hivirtus.floatingmenu.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hivirtus.floatingmenu.R
import com.hivirtus.floatingmenu.databinding.ActivityLoginBinding
import com.hivirtus.floatingmenu.util.Prefs

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appVersion.text = "App Version: 1.0.0-globe-arm64-hivirtus"
        binding.userEdit.setText(Prefs.getUser(this))
        binding.pwdEdit.setText(Prefs.getPassword(this))

        binding.visPwd.setOnClickListener {
            passwordVisible = !passwordVisible
            binding.pwdEdit.transformationMethod = if (passwordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            binding.pwdEdit.setSelection(binding.pwdEdit.text?.length ?: 0)
        }

        binding.loginUser.setOnClickListener {
            val user = binding.userEdit.text?.toString()?.trim() ?: ""
            val pass = binding.pwdEdit.text?.toString()?.trim() ?: ""
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, R.string.login_empty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Prefs.saveLogin(this, user, pass)
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
        }

        binding.enroll.setOnClickListener {
            Toast.makeText(this, R.string.register_stub, Toast.LENGTH_SHORT).show()
        }

        binding.recharge.setOnClickListener {
            Toast.makeText(this, R.string.activation_stub, Toast.LENGTH_SHORT).show()
        }
    }
}

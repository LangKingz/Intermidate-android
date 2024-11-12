package com.example.story.view.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.story.databinding.ActivityWelcomeBinding
import com.example.story.view.auth.login.LoginActivity
import com.example.story.view.auth.register.RegisterActivity
import com.example.story.view.dashboard.MainActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkStatus()

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.textView, "translationY", 0f, -200f).apply {
            duration = 1000
            start()
        }.start()

        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(together)
            start()
        }
    }

    private fun checkStatus() {
        val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", null)

        if (token != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
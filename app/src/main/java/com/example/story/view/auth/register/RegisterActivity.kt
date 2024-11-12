package com.example.story.view.auth.register

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.story.data.Response.Register
import com.example.story.data.Response.RegisterRequest
import com.example.story.data.RetrofitClient
import com.example.story.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar4.visibility = View.GONE
        binding.btnRegis.setOnClickListener {
            val name = binding.name.text.trim().toString()
            val email = binding.email.text?.trim().toString()
            val password = binding.password.text?.trim().toString()
            perfomRegis(name, email, password)

        }

    }

    private fun perfomRegis(name: String, email: String, password: String) {
        binding.progressBar4.visibility = View.VISIBLE
        val requestRegis = RegisterRequest(name, email, password)
        val call = RetrofitClient.instance.register(requestRegis)

        call.enqueue(object : Callback<Register> {
            override fun onResponse(call: Call<Register>, response: Response<Register>) {
                if (response.isSuccessful) {
                    binding.progressBar4.visibility = View.GONE
                    val regisres = response.body()
                    Toast.makeText(this@RegisterActivity, regisres?.message, Toast.LENGTH_SHORT)
                        .show()
                    Log.d("RegisterActivity", "onResponse: $regisres")
                    finish()
                }
            }

            override fun onFailure(call: Call<Register>, t: Throwable) {
                Log.d("RegisterActivity", "onFailure: ${t.message}")
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }
}
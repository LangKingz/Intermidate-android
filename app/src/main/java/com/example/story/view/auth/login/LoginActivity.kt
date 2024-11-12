package com.example.story.view.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.story.data.Response.Login
import com.example.story.data.Response.LoginRequest
import com.example.story.data.RetrofitClient
import com.example.story.databinding.ActivityLoginBinding
import com.example.story.view.dashboard.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.GONE

        binding.btnRegis.setOnClickListener {
            val email = binding.email.text?.trim().toString()
            val password = binding.password.text?.trim().toString()
            LoginPerfom(email, password)
        }

    }

     fun LoginPerfom(email: String, password: String) {
       binding.progressBar.visibility = View.VISIBLE
        val loginrequest = LoginRequest(email, password)
        val call = RetrofitClient.instance.login(loginrequest)

        call.enqueue(object : Callback<Login>{
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if (response.isSuccessful){
                    binding.progressBar.visibility = View.GONE
                    val loginres = response.body()
                    Toast.makeText(this@LoginActivity, loginres?.message, Toast.LENGTH_SHORT).show()
                    if (loginres != null && loginres.error == false){
//                        menyimpan token .nama dan id ke shared preference
                        val token = loginres.loginResult?.token
                        val name = loginres.loginResult?.name
                        val userId = loginres.loginResult?.userId

                        val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("TOKEN", token)
                        editor.putString("NAME", name)
                        editor.putString("ID", userId)

                        editor.apply()

                        Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                        menghapus stack intent
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }else {
                        Toast.makeText(this@LoginActivity, "Login issue : ${loginres?.message}", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                Log.d("LoginActivity", "onFailure: ${t.message}")
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Gagal Login : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
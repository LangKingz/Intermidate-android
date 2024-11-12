package com.example.story.view.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.story.databinding.ActivityMainBinding
import com.example.story.helper.AdapterList
import com.example.story.view.auth.WelcomeActivity
import com.example.story.view.auth.login.LoginActivity
import com.example.story.view.map.MapsActivity
import com.example.story.view.tambah.TambahActivity
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private lateinit var adapter: AdapterList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        checkStatus()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
        val name = sharedPref.getString("NAME", "no user detected")
        val token = sharedPref.getString("TOKEN", null)

        binding.username.text = "Hello , $name"
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdapterList()
        binding.recyclerView.adapter = adapter

        binding.btnlogOut.setOnClickListener {
            logOut()
        }

        binding.btnMap.setOnClickListener {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        binding.btnTambah.setOnClickListener {
            val intent = Intent(this@MainActivity, TambahActivity::class.java)
            startActivity(intent)
        }

        Log.d(TAG, "token: $token")

        token?.let {
            observePagedData(it)
        }

    }

    private fun observePagedData(token: String) {
        lifecycleScope.launchWhenStarted {
            binding.progressBar2.visibility = View.VISIBLE
            viewModel.fetchPagedStories(token).collectLatest { pagingData ->
                binding.progressBar2.visibility = View.GONE
                adapter.submitData(pagingData) // Kirim data paging ke adapter
            }
        }
    }


    private fun logOut() {
        val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Menghapus stack activity
        startActivity(intent)
    }

    fun checkStatus() {
        val sharedPref = getSharedPreferences("AUTH", MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", null)
        if (token == null) {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
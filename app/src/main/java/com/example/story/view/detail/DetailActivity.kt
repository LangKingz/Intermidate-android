package com.example.story.view.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.story.data.RetrofitClient
import com.example.story.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        val id = intent.getStringExtra(EXTRA_ID)

        val pref = getSharedPreferences("AUTH", MODE_PRIVATE)
        val token = pref.getString("TOKEN", null)

        fetchDetail(id.toString(), token.toString())

    }

    fun fetchDetail(id: String, token: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getStorybyId(id, "Bearer $token")
                val detailStory = response.story

                binding.namaUser.text = "Pembuat : ${detailStory?.name}"
                binding.deskripsiDetail.text = "Deskripsi : ${detailStory?.description}"
                Glide.with(this@DetailActivity)
                    .load(detailStory?.photoUrl)
                    .into(binding.imageView)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@DetailActivity,
                    "fetched failed : ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}
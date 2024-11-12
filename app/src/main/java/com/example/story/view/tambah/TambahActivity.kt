package com.example.story.view.tambah

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.story.data.RetrofitClient
import com.example.story.databinding.ActivityTambahBinding
import com.example.story.helper.UriToFile
import com.example.story.helper.getImageUri
import com.example.story.helper.reduceFileImage
import com.example.story.view.dashboard.MainActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class TambahActivity : AppCompatActivity() {

    private var currentImage: Uri? = null
    private lateinit var binding: ActivityTambahBinding

//    Permintaan IZIN KAMERA

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            currentImage = getImageUri(this)
            Toast.makeText(this, "Permission request granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        REQUIRED_PERMISSIONS
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }

        binding.progressBar3.visibility = View.GONE

        binding.btnBackMenu.setOnClickListener {
            finish()
        }

        binding.btnGallery.setOnClickListener {
            startGalerry()
        }

        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        binding.uploud.setOnClickListener {
            if (currentImage != null) {
                addStory()
            } else {
                showToast("harap isi dulu gambarnya dan deskripsi")
            }
        }
    }

    private fun addStory() {
        currentImage?.let { uri->
            val imageFile = UriToFile(uri, this).reduceFileImage()
            val descriptionText = binding.descField.text.toString()
            val token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", null)

            val requestBody = descriptionText.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.addStory("Bearer $token",multipartBody,requestBody)
                    if (response.error == false){
                        showToast("Story added")
                        val intent = Intent(this@TambahActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }else{
                        showToast("Error : ${response.message}")
                    }
                }catch (e: Exception){
                    showToast("Error : Silahkan Isi semua")
                    e.printStackTrace()
                }
            }
        }?:showToast("Image not found")
    }

    private fun startGalerry() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImage = uri
            showImage(uri)
        } else showToast("Image not found")
    }

    private fun startCamera() {
        currentImage = getImageUri(this)
        if (currentImage != null) {
            launcherCamera.launch(currentImage!!)
        } else {
            showToast("Image not found")
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage(currentImage!!)
        } else {
//
            currentImage = null
            showToast("Image not found")
        }
    }

    private fun showImage(uri: Uri) {
        binding.previewImage.setImageURI(uri)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUIRED_PERMISSIONS = android.Manifest.permission.CAMERA
    }
}
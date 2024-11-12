package com.example.story.view.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.story.data.Response.ListStoryItem
import com.example.story.data.Response.Story
import com.example.story.data.RetrofitClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.story.databinding.ActivityMapsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Kembali.setOnClickListener {
            finish()
        }

        val token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "")

        Log.d(TAG, "onMapReady: $token")

        val mapFragment = supportFragmentManager
            .findFragmentById(com.example.story.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        val token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "")

        fetchStoryMap(token.toString())
    }

    private fun fetchStoryMap(token: String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getStroiesMap("Bearer $token")
                Log.d(TAG, "fetchStoryMap: $response")
                if (!response.error!!){
                    withContext(Dispatchers.Main){
                        displayStoryMarkers(response.listStory!!)
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun displayStoryMarkers(stories: List<ListStoryItem>) {
        for (story in stories) {
            if (story.lat != null && story.lon != null) {
                val location = LatLng(story.lat as Double, story.lon as Double)
                mMap.addMarker(MarkerOptions().position(location).title("${story.name} - ${story.description}"))
            } else {
                Log.e("MapsActivity", "Story ${story.id} has null coordinates.")
            }
        }

        if (stories.isNotEmpty()) {
            val firstLocation = LatLng(stories[0].lat as Double, stories[0].lon as Double)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f))
        }
    }

    companion object{
        private const val TAG = "MapsActivity"
    }
}
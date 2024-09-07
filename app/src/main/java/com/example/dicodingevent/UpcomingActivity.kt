package com.example.dicodingevent

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.databinding.ActivityUpcomingBinding
import com.google.android.material.snackbar.Snackbar

class UpcomingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpcomingBinding
    private val viewModel: UpcomingViewModel by viewModels()
    private lateinit var adapter: EventAdapter
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var snackbar: Snackbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpcomingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupRecyclerView()
        setupNetworkCallback()
        observeViewModel()

        // Fetch events
        viewModel.fetchEvents(0) // 0 for past events, 1 for active events
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter()
        binding.rvEvents.adapter = adapter
        binding.rvEvents.layoutManager = LinearLayoutManager(this)
    }

    private fun setupNetworkCallback() {
        connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread {
                    viewModel.setNetworkState(true)
                    snackbar?.dismiss()
                    viewModel.fetchEvents(0)
                }
            }

            override fun onLost(network: Network) {
                runOnUiThread {
                    viewModel.setNetworkState(false)
                    showNetworkErrorSnackbar()
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun observeViewModel() {
        viewModel.events.observe(this) { events ->
            adapter.submitList(events)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.networkState.observe(this) { isConnected ->
            if (!isConnected) {
                showNetworkErrorSnackbar()
            } else {
                snackbar?.dismiss()
            }
        }
    }

    private fun showNetworkErrorSnackbar() {
        snackbar = Snackbar.make(
            binding.root,
            "Network not detected. Please check your internet connection.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
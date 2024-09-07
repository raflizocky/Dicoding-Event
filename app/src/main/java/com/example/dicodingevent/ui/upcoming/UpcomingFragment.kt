package com.example.dicodingevent.ui.upcoming

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.EventAdapter
import com.example.dicodingevent.EventViewModel
import com.example.dicodingevent.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar

class UpcomingFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by activityViewModels()
    private lateinit var adapter: EventAdapter
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var snackbar: Snackbar? = null
    private var isNetworkAvailable = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupNetworkCallback()
        observeViewModel()
        updateNetworkAvailability()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter()
        binding.rvEvents.adapter = adapter
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupNetworkCallback() {
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onAvailable(network: Network) {
                activity?.runOnUiThread {
                    updateNetworkAvailability(true)
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onLost(network: Network) {
                activity?.runOnUiThread {
                    updateNetworkAvailability(false)
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkNetworkAvailability(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateNetworkAvailability(available: Boolean? = null) {
        val networkAvailable = available ?: checkNetworkAvailability()
        isNetworkAvailable = networkAvailable
        viewModel.setNetworkState(networkAvailable)
        if (networkAvailable) {
            viewModel.fetchEvents(1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun observeViewModel() {
        viewModel.events.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
            updateNoDataVisibility(events.isEmpty())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.rvEvents.isVisible = !isLoading
            if (isLoading) {
                binding.tvNoData.isVisible = false
            }
        }

        viewModel.networkState.observe(viewLifecycleOwner) { isConnected ->
            if (!isConnected) {
                showNetworkErrorSnackbar()
            } else {
                snackbar?.dismiss()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null && isNetworkAvailable()) {
                showErrorSnackbar(errorMessage)
            }
        }
    }

    private fun updateNoDataVisibility(showNoData: Boolean) {
        binding.tvNoData.isVisible = showNoData
        binding.rvEvents.isVisible = !showNoData
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showNetworkErrorSnackbar() {
        snackbar?.dismiss()  // Dismiss any existing Snackbar
        snackbar = Snackbar.make(
            binding.root,
            "Network not detected. Please check your internet connection.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("Retry") {
                updateNetworkAvailability()
            }
            show()
        }
    }

    private fun showErrorSnackbar(message: String) {
        snackbar?.dismiss()  // Dismiss any existing Snackbar
        snackbar = Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        _binding = null
    }
}
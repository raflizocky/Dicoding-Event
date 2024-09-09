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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.data.UiState
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.ui.EventAdapter
import com.example.dicodingevent.ui.EventViewModel
import com.example.dicodingevent.databinding.FragmentUpcomingBinding
import com.example.dicodingevent.ui.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpcomingFragment : Fragment() {
    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels { ViewModelFactory.getInstance(requireContext()) }
    private lateinit var adapter: EventAdapter
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var snackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupNetworkCallback()
        observeViewModel()
        checkNetworkAndFetchEvents()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter()
        binding.rvEvents.apply {
            this.adapter = this@UpcomingFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupNetworkCallback() {
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.setNetworkState(true)
                    viewModel.fetchEvents(1)
                }
            }

            override fun onLost(network: Network) {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.setNetworkState(false)
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkNetworkAndFetchEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            val isNetworkAvailable = isNetworkAvailable()
            viewModel.setNetworkState(isNetworkAvailable)
            if (isNetworkAvailable) {
                viewModel.fetchEvents(1)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> showLoading()
                    is UiState.Success -> showEvents(state.data)
                    is UiState.Error -> showError(state.message)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.networkState.collect { isConnected ->
                if (!isConnected) {
                    showNetworkErrorSnackbar()
                } else {
                    snackbar?.dismiss()
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.rvEvents.isVisible = false
        binding.tvNoData.isVisible = false
    }

    private fun showEvents(events: List<ListEventsItem>) {
        binding.progressBar.isVisible = false
        binding.rvEvents.isVisible = true
        binding.tvNoData.isVisible = events.isEmpty()
        adapter.submitList(events)
    }

    private fun showError(message: String) {
        binding.progressBar.isVisible = false
        binding.rvEvents.isVisible = false
        binding.tvNoData.isVisible = true
        showErrorSnackbar(message)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showNetworkErrorSnackbar() {
        snackbar?.dismiss()
        snackbar = Snackbar.make(
            binding.root,
            "Network not detected. Please check your internet connection.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("Retry") {
                checkNetworkAndFetchEvents()
            }
            show()
        }
    }

    private fun showErrorSnackbar(message: String) {
        snackbar?.dismiss()
        snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).apply { show() }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun isNetworkAvailable(): Boolean = withContext(Dispatchers.IO) {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        _binding = null
    }
}
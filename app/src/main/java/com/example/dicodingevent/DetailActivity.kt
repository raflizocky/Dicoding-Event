package com.example.dicodingevent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import androidx.core.view.isVisible
import com.example.dicodingevent.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EVENT_DETAIL = "event_detail"
    }
    private lateinit var binding: ActivityDetailBinding

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra(EVENT_DETAIL) ?: return finish()

        viewModel.fetchEventDetails(eventId)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.event.observe(this) { event ->
            event?.let { displayEventDetails(it) }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.cardView.isVisible = !isLoading
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            showSnackbar(errorMessage)
        }
    }

    private fun displayEventDetails(event: Event) {
        with(binding) {
            Glide.with(this@DetailActivity)
                .load(event.mediaCover)
                .into(ivEventImage)

            tvEventName.text = event.name
            tvEventDate.text = formatDateTime(event.beginTime ?: "")
            tvEventLocation.text = "Sisa Kuota: ${(event.quota ?: 0) - (event.registrants ?: 0)}"
            tvEventDescription.text = HtmlCompat.fromHtml(
                event.description ?: "",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            btnRegister.setOnClickListener {
                event.link?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
            }
        }
    }

    private fun formatDateTime(dateTimeString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        return try {
            val date = inputFormat.parse(dateTimeString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateTimeString
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.main, message, Snackbar.LENGTH_LONG).show()
    }
}
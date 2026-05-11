package com.lhh.mvvmsample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lhh.mvvmsample.databinding.ActivityMainBinding
import com.lhh.mvvmsample.databinding.DialogServerSettingsBinding
import com.lhh.mvvmsample.ui.ImageListViewModel
import com.lhh.mvvmsample.ui.adapter.ImageListAdapter
import com.lhh.mvvmsample.ui.viewer.ImageViewerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ImageListViewModel by viewModels()
    private val imageListAdapter = ImageListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbarMenu()
        setupRecyclerView()
        setupRefresh()
        setupSettingsButton()
        collectUiState()
    }

    private fun setupToolbarMenu() {
        binding.topToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_select_all -> {
                    viewModel.toggleSelectAllVisible()
                    true
                }
                R.id.menu_download -> {
                    viewModel.downloadSelected()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerImages.layoutManager = LinearLayoutManager(this)
        binding.recyclerImages.adapter = imageListAdapter
        imageListAdapter.onRowClick = { entity ->
            startActivity(
                ImageViewerActivity.createIntent(this, entity.url, entity.name),
            )
        }
        imageListAdapter.onToggleSelect = { url ->
            viewModel.toggleSelection(url)
        }
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.onRefresh()
        }
    }

    private fun setupSettingsButton() {
        binding.buttonSettings.setOnClickListener {
            showServerSettingsDialog()
        }
    }

    private fun showServerSettingsDialog() {
        val state = viewModel.uiState.value
        val dialogBinding = DialogServerSettingsBinding.inflate(layoutInflater)
        dialogBinding.editServerIp.setText(state.serverIp)
        dialogBinding.editServerPort.setText(state.serverPort)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.server_config)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val ip = dialogBinding.editServerIp.text?.toString().orEmpty()
                val port = dialogBinding.editServerPort.text?.toString().orEmpty()
                viewModel.saveServerConfig(ip, port)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val allVisibleSelected =
                        state.images.isNotEmpty() &&
                            state.images.all { it.url in state.selectedUrls }
                    binding.topToolbar.menu.findItem(R.id.menu_select_all)?.title =
                        getString(
                            if (allVisibleSelected) {
                                R.string.deselect_all
                            } else {
                                R.string.select_all
                            },
                        )
                    imageListAdapter.setSelectedUrls(state.selectedUrls)
                    imageListAdapter.submitList(state.images)
                    binding.swipeRefresh.isRefreshing = state.isRefreshing
                    binding.progressLoading.visibility =
                        if (state.isLoading && state.images.isEmpty()) View.VISIBLE else View.GONE
                    binding.progressDownloading.visibility =
                        if (state.isDownloading) View.VISIBLE else View.GONE
                    binding.textEmpty.visibility =
                        if (!state.isLoading && state.images.isEmpty()) View.VISIBLE else View.GONE
                    binding.textServerHint.text = getString(
                        R.string.server_hint_format,
                        state.serverIp.ifEmpty { "未设置" },
                        state.serverPort,
                    )
                    state.errorMessage?.let { error ->
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                        viewModel.clearErrorMessage()
                    }
                    state.toastMessage?.let { message ->
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                        viewModel.clearToastMessage()
                    }
                }
            }
        }
    }
}

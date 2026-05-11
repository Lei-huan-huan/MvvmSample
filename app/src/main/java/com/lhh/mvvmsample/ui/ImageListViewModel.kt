package com.lhh.mvvmsample.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhh.mvvmsample.data.download.GalleryImageWriter
import com.lhh.mvvmsample.data.local.ImageEntity
import com.lhh.mvvmsample.data.local.ImageSelectionStore
import com.lhh.mvvmsample.data.local.ServerConfigStore
import com.lhh.mvvmsample.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class ImageListViewModel @Inject constructor(
    private val repository: ImageRepository,
    private val serverConfigStore: ServerConfigStore,
    private val selectionStore: ImageSelectionStore,
    private val galleryImageWriter: GalleryImageWriter,
) : ViewModel() {

    private var latestImagesByUrl: Map<String, ImageEntity> = emptyMap()
    private val _uiState = MutableStateFlow(ImageListUiState())
    val uiState: StateFlow<ImageListUiState> = _uiState

    init {
        loadServerConfig()
        observeSelection()
        observeLocalImages()
        ensureLoaded()
    }

    private fun loadServerConfig() {
        _uiState.update { current ->
            current.copy(
                serverIp = serverConfigStore.getServerIp(),
                serverPort = serverConfigStore.getServerPort().toString(),
            )
        }
    }

    private fun observeSelection() {
        viewModelScope.launch {
            selectionStore.selectedUrls.collectLatest { urls ->
                _uiState.update { it.copy(selectedUrls = urls) }
            }
        }
    }

    private fun observeLocalImages() {
        viewModelScope.launch {
            repository.images.collectLatest { images ->
                cacheImages(images)
                val valid = images.map { it.url }.toSet()
                selectionStore.pruneToValid(valid)
                _uiState.update { current ->
                    current.copy(
                        images = images,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun cacheImages(images: List<ImageEntity>) {
        latestImagesByUrl = images.associateBy { it.url }
    }

    private fun ensureLoaded() {
        viewModelScope.launch {
            runCatching { repository.ensureLoaded() }
                .onFailure {
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = it.message ?: "首次加载失败",
                        )
                    }
                }
        }
    }

    fun toggleSelection(url: String) {
        selectionStore.toggle(url)
    }

    fun toggleSelectAllVisible() {
        selectionStore.toggleSelectAllVisible(_uiState.value.images.map { it.url })
    }

    fun onRefresh() {
        if (_uiState.value.isRefreshing) return
        viewModelScope.launch {
            _uiState.update { current -> current.copy(isRefreshing = true) }
            runCatching { repository.refresh() }
                .onFailure {
                    _uiState.update { current ->
                        current.copy(errorMessage = it.message ?: "刷新失败")
                    }
                }
            _uiState.update { current -> current.copy(isRefreshing = false) }
        }
    }

    fun downloadSelected() {
        val urls = _uiState.value.selectedUrls.toList()
        if (urls.isEmpty()) {
            _uiState.update { it.copy(toastMessage = "请先勾选要下载的图片") }
            return
        }
        val byUrl = latestImagesByUrl
        viewModelScope.launch {
            _uiState.update { it.copy(isDownloading = true) }
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    var ok = 0
                    for (url in urls) {
                        val entity = byUrl[url] ?: continue
                        galleryImageWriter.saveFromNetwork(entity)
                        ok++
                    }
                    ok
                }
            }
            result.fold(
                onSuccess = { count ->
                    _uiState.update {
                        it.copy(
                            isDownloading = false,
                            toastMessage = "已保存 $count 张到相册（Pictures/MvvmImageClient）",
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isDownloading = false,
                            errorMessage = e.message ?: "下载失败",
                        )
                    }
                },
            )
        }
    }

    fun clearErrorMessage() {
        _uiState.update { current -> current.copy(errorMessage = null) }
    }

    fun clearToastMessage() {
        _uiState.update { current -> current.copy(toastMessage = null) }
    }

    fun saveServerConfig(ip: String, portText: String) {
        val cleanIp = ip.trim()
        if (cleanIp.isEmpty()) {
            _uiState.update { current -> current.copy(errorMessage = "IP 不能为空") }
            return
        }
        val port = portText.trim().toIntOrNull()
        if (port == null || port !in 1..65535) {
            _uiState.update { current -> current.copy(errorMessage = "端口必须是 1-65535") }
            return
        }

        serverConfigStore.saveServerConfig(cleanIp, port)
        _uiState.update { current ->
            current.copy(
                serverIp = cleanIp,
                serverPort = port.toString(),
                toastMessage = "已保存服务器地址",
            )
        }
        onRefresh()
    }

    fun hasImage(url: String): Boolean {
        return latestImagesByUrl[url] != null
    }
}

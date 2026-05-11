package com.lhh.mvvmsample.ui.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhh.mvvmsample.data.local.ImageSelectionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    private val selectionStore: ImageSelectionStore,
) : ViewModel() {

    private val _imageUrl = MutableStateFlow<String?>(null)

    val isSelected: StateFlow<Boolean> =
        _imageUrl
            .flatMapLatest { url ->
                if (url == null) {
                    flowOf(false)
                } else {
                    selectionStore.selectedUrls.map { url in it }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _chromeVisible = MutableStateFlow(true)
    val chromeVisible: StateFlow<Boolean> = _chromeVisible.asStateFlow()

    fun bindImageUrl(url: String) {
        _imageUrl.value = url
    }

    fun toggleChrome() {
        _chromeVisible.update { !it }
    }

    fun toggleSelection() {
        val url = _imageUrl.value ?: return
        selectionStore.toggle(url)
    }
}

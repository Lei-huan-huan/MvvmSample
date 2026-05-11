package com.lhh.mvvmsample.data.local

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class ImageSelectionStore @Inject constructor() {

    private val _selectedUrls = MutableStateFlow<Set<String>>(emptySet())
    val selectedUrls: StateFlow<Set<String>> = _selectedUrls.asStateFlow()

    fun toggle(url: String) {
        _selectedUrls.update { cur -> if (url in cur) cur - url else cur + url }
    }

    fun selectAll(urls: Collection<String>) {
        _selectedUrls.value = urls.toSet()
    }

    /** 当前列表项若已全部选中则取消这些项，否则选中当前列表全部 */
    fun toggleSelectAllVisible(visibleUrls: Collection<String>) {
        val urls = visibleUrls.toSet()
        if (urls.isEmpty()) return
        _selectedUrls.update { cur ->
            if (urls.all { it in cur }) {
                cur - urls
            } else {
                cur.union(urls)
            }
        }
    }

    fun clear() {
        _selectedUrls.value = emptySet()
    }

    /** 列表刷新后去掉已不存在的项 */
    fun pruneToValid(validUrls: Set<String>) {
        _selectedUrls.update { it.intersect(validUrls) }
    }
}

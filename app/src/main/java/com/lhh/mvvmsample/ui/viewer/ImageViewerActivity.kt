package com.lhh.mvvmsample.ui.viewer

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.lhh.mvvmsample.R
import com.lhh.mvvmsample.databinding.ActivityImageViewerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImageViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageViewerBinding
    private val viewModel: ImageViewerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.chromeOverlay) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.setPadding(v.paddingLeft, bars.top, v.paddingRight, v.paddingBottom)
            insets
        }

        val imageUrl = intent.getStringExtra(ImageViewerExtras.IMAGE_URL)
            ?: run {
                finish()
                return
            }
        val imageName = intent.getStringExtra(ImageViewerExtras.IMAGE_NAME).orEmpty()
        viewModel.bindImageUrl(imageUrl)

        binding.textTitle.text = imageName.ifEmpty { imageUrl.substringAfterLast('/') }

        Glide.with(binding.photoView)
            .load(imageUrl)
            .fitCenter()
            .into(binding.photoView)

        binding.buttonBack.setOnClickListener { finish() }

        binding.buttonSelectToggle.setOnClickListener { viewModel.toggleSelection() }

        binding.photoView.setOnPhotoTapListener { _, _, _ ->
            viewModel.toggleChrome()
        }
        binding.photoView.setOnOutsidePhotoTapListener {
            viewModel.toggleChrome()
        }

        collectUiState()
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isSelected.collect { selected ->
                        applySelectionButtonUi(selected)
                    }
                }
                launch {
                    viewModel.chromeVisible.collect { visible ->
                        binding.chromeOverlay.visibility = if (visible) View.VISIBLE else View.GONE
                        applyImmersive(!visible)
                    }
                }
            }
        }
    }

    private fun applySelectionButtonUi(selected: Boolean) {
        val b = binding.buttonSelectToggle
        val white = ContextCompat.getColor(this, android.R.color.white)
        val strokePx = (2f * resources.displayMetrics.density).roundToInt().coerceAtLeast(2)
        val iconPad = (6f * resources.displayMetrics.density).roundToInt()
        if (selected) {
            b.text = getString(R.string.viewer_selected_label)
            b.setIconResource(R.drawable.ic_done_white_24)
            b.iconTint = ColorStateList.valueOf(white)
            b.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            b.iconPadding = iconPad
            b.iconSize = (22f * resources.displayMetrics.density).roundToInt()
            b.strokeWidth = 0
            b.strokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
            b.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.viewer_select_on))
            b.setTextColor(white)
        } else {
            b.text = getString(R.string.viewer_tap_to_select)
            b.icon = null
            b.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            b.strokeWidth = strokePx
            b.strokeColor = ColorStateList.valueOf(white)
            b.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.viewer_select_off_fill))
            b.setTextColor(white)
        }
    }

    private fun applyImmersive(hideSystemBars: Boolean) {
        val controller = WindowInsetsControllerCompat(window, binding.rootViewer)
        if (hideSystemBars) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
            @Suppress("DEPRECATION")
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    companion object {
        fun createIntent(context: Context, imageUrl: String, imageName: String): Intent =
            Intent(context, ImageViewerActivity::class.java).apply {
                putExtra(ImageViewerExtras.IMAGE_URL, imageUrl)
                putExtra(ImageViewerExtras.IMAGE_NAME, imageName)
            }
    }
}

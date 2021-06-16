/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.ActivityPostBinding
import im.point.dotty.model.PostType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FlowPreview
class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var viewModel: PostViewModel
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(this::class.simpleName, exception.message, exception)
        showSnackbar(exception.localizedMessage)
    }

    companion object {
        const val POST_ID = "post-id"
        const val POST_TYPE = "post-type"

        fun getIntent(context: Context, post: PostType, id: String): Intent {
            val intent = Intent(context, PostActivity::class.java)
            intent.putExtra(POST_ID, id)
            intent.putExtra(POST_TYPE, post)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val postId = intent.getStringExtra(POST_ID)!!
        val post = intent.getSerializableExtra(POST_TYPE) as PostType
        viewModel = ViewModelProvider(this, ViewModelFactory(this, post, postId))
                .get(PostViewModel::class.java)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        lifecycleScope.launchWhenStarted {
            binding.postSubscribe.setOnCheckedChangeListener { _, isChecked ->
                launch(exceptionHandler) { viewModel.onSubscribeChecked(isChecked).collect { } }
            }
            viewModel.isSubscribed.collect { binding.postSubscribe.isChecked = it }
        }
        lifecycleScope.launchWhenStarted {
            binding.postRecommend.setOnCheckedChangeListener { _, isChecked ->
                launch(exceptionHandler) { viewModel.onRecommendChecked(isChecked).collect() }
            }
            viewModel.isRecommended.collect { binding.postRecommend.isChecked = it }
        }
        lifecycleScope.launchWhenStarted {
            binding.postBookmark.setOnCheckedChangeListener { _, isChecked ->
                launch(exceptionHandler) { viewModel.onBookmarkChecked(isChecked).collect() }
            }
            viewModel.isBookmarked.collect { binding.postBookmark.isChecked = it }
        }
        lifecycleScope.launchWhenStarted {
            binding.postPin.setOnCheckedChangeListener { _, isChecked ->
                launch(exceptionHandler) { viewModel.onPinChecked(isChecked).collect() }
            }
            viewModel.isPinned.collect { binding.postPin.isChecked = it }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isPinVisible.collect {
                binding.postPin.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.post.collect { post ->
                binding.toolbar.title = post.nameOrLogin
            }
        }
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(this.findViewById(R.id.post_layout), text, Snackbar.LENGTH_LONG).show()
    }
}


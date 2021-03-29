/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var viewModel: PostViewModel
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
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
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(exceptionHandler) {
            viewModel.getPost().collect { post ->
                binding.toolbar.title = post.nameOrLogin
                binding.postBookmark.isChecked = post.bookmarked == true
                binding.postRecommend.isChecked = post.recommended == true
                binding.postSubscribe.isChecked = post.subscribed == true
                binding.postPin.isChecked = post.pinned == true
            }
        }
        lifecycleScope.launch(exceptionHandler) {
            viewModel.isPinVisible.consumeEach { binding.postPin.visibility = if (it) View.VISIBLE else View.GONE }
        }
        binding.postSubscribe.setOnCheckedChangeListener { view, isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                if (isChecked) viewModel.unsubscribe().await() else viewModel.subscribe().await()
            }
        }
        binding.postRecommend.setOnCheckedChangeListener { view, isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                if (isChecked) viewModel.unrecommend().await() else viewModel.recommend().await()
            }
        }
        binding.postBookmark.setOnCheckedChangeListener { view, isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                if (isChecked) viewModel.unbookmark().await() else viewModel.bookmark().await()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(this.findViewById(R.id.post_layout), text, Snackbar.LENGTH_LONG).show()
    }
}


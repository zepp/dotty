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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var postId: String
    private lateinit var from: From
    private lateinit var viewModel: PostViewModel
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        showSnackbar(exception.localizedMessage)
    }

    companion object {
        const val POST_ID = "post-id"
        const val POST_FROM = "post-from"

        fun getIntent(context: Context, from: From, id: String): Intent {
            val intent = Intent(context, PostActivity::class.java)
            intent.putExtra(POST_ID, id)
            intent.putExtra(POST_FROM, from)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = intent.getStringExtra(POST_ID)!!
        from = intent.getSerializableExtra(POST_FROM) as From
        viewModel = ViewModelProvider(this, ViewModelFactory(this, postId))
                .get(PostViewModel::class.java)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (supportFragmentManager.backStackEntryCount == 0) {
            val fragment = PostFragment.newInstance(intent.getSerializableExtra(POST_FROM) as From,
                    intent.getStringExtra(POST_ID)!!)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.post_container, fragment)
                    .addToBackStack(fragment::class.simpleName)
                    .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(exceptionHandler) {
            when (from) {
                From.FROM_RECENT -> viewModel.getRecentPost()
                From.FROM_COMMENTED -> viewModel.getCommentedPost()
                From.FROM_ALL -> viewModel.getAllPost()
                From.FROM_USER -> viewModel.getUserPost()
            }.collect { post ->
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

enum class From {
    FROM_RECENT,
    FROM_COMMENTED,
    FROM_ALL,
    FROM_USER
}
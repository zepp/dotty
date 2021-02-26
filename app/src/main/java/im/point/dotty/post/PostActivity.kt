/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.RxActivity
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.ActivityPostBinding
import im.point.dotty.model.Post

class PostActivity : RxActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var postId: String
    private lateinit var from: From
    private lateinit var viewModel: PostViewModel

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
        viewModel = ViewModelProvider(this, ViewModelFactory(this))
                .get(PostViewModel::class.java)
        viewModel.postId = postId
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
        addDisposable(when (from) {
            From.FROM_RECENT -> viewModel.getRecentPost()
            From.FROM_COMMENTED -> viewModel.getCommentedPost()
            From.FROM_ALL -> viewModel.getAllPost()
        }.subscribe { post: Post ->
            binding.toolbar.title = post.nameOrLogin
            binding.postBookmark.isChecked = post.bookmarked == true
            binding.postRecommend.isChecked = post.recommended == true
            binding.postSubscribe.isChecked = post.subscribed == true
            binding.postPin.isChecked = post.pinned == true
            binding.postBookmark.setOnCheckedChangeListener { view, isChecked ->
                addDisposable((if (isChecked) viewModel.unbookmark() else viewModel.bookmark())
                        .subscribe({}, { error -> error.message?.let { showSnackbar(it) } }))
            }
            binding.postRecommend.setOnCheckedChangeListener { view, isChecked ->
                addDisposable((if (isChecked) viewModel.unrecommend() else viewModel.recommend())
                        .subscribe({}, { error -> error.message?.let { showSnackbar(it) } }))
            }
            binding.postBookmark.setOnCheckedChangeListener { view, isChecked ->
                addDisposable((if (isChecked) viewModel.unbookmark() else viewModel.bookmark())
                        .subscribe({}, { error -> error.message?.let { showSnackbar(it) } }))
            }
        })
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
    FROM_ALL
}
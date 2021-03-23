/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */

package im.point.dotty.user

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
import im.point.dotty.databinding.ActivityUserBinding
import im.point.dotty.feed.FeedAdapter
import im.point.dotty.model.UserPost
import im.point.dotty.post.From
import im.point.dotty.post.PostActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: FeedAdapter<UserPost>
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        showSnackbar(exception.localizedMessage)
        Log.e(localClassName, "error: ", exception)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ViewModelFactory(this, intent.getLongExtra(USER_ID, -1)))
                .get(UserViewModel::class.java)
        adapter = FeedAdapter()
        adapter.onItemClicked = { item -> startActivity(PostActivity.getIntent(this, From.FROM_USER, item.id)) }
        binding.userPosts.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch(exceptionHandler) {
            viewModel.isActionsVisible.consumeEach { value ->
                binding.userActions.visibility = if (value) View.VISIBLE else View.GONE
            }
            viewModel.fetchUserAndPosts().collect { onFetched() }
            viewModel.getUser().collect { user ->
                binding.userName.text = user.name
                binding.userAbout.text = user.about
            }
        }

        lifecycleScope.launch(exceptionHandler) {
            viewModel.getPosts().collect { items -> adapter.list = items }
        }

        binding.userSubscribe.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                if (isChecked) viewModel.unsubscribe().await() else viewModel.subscribe().await()
            }
        }
        binding.userRecommendSubscribe.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                if (isChecked) viewModel.unsubscribeRecommendations().await()
                else viewModel.subscribeRecommendations().await()
            }
        }
        binding.userBlock.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(exceptionHandler) {
                if (isChecked) viewModel.unblock().await() else viewModel.block().await()
            }
        }
        binding.userRefresh.setOnRefreshListener {
            lifecycleScope.launch(exceptionHandler) {
                viewModel.fetchUserAndPosts().collect { onFetched() }
            }
        }
    }

    private fun onFetched() {
        binding.userRefresh.isRefreshing = false
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(findViewById(R.id.user_layout), text, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private const val USER_ID = "user-id"

        fun getIntent(context: Context, userId: Long): Intent {
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(USER_ID, userId)
            return intent
        }
    }
}
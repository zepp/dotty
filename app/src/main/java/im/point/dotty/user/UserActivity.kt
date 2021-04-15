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
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.AvatarOutline
import im.point.dotty.common.RecyclerItemDecorator
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.ActivityUserBinding
import im.point.dotty.feed.FeedAdapter
import im.point.dotty.model.PostType
import im.point.dotty.model.UserPost
import im.point.dotty.post.PostActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: FeedAdapter<UserPost>
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(this::class.simpleName, exception.message, exception)
        showSnackbar(exception.localizedMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ViewModelFactory(this, intent.getLongExtra(USER_ID, -1)))
                .get(UserViewModel::class.java)
        adapter = FeedAdapter(lifecycleScope, viewModel::getCommentAvatar)
        adapter.onItemClicked = { item -> startActivity(PostActivity.getIntent(this, PostType.USER_POST, item.id)) }
        binding.userPosts.adapter = adapter
        binding.userPosts.addItemDecoration(RecyclerItemDecorator(this, DividerItemDecoration.VERTICAL, 4))
        binding.userAvatar.outlineProvider = AvatarOutline(64)
        binding.userAvatar.clipToOutline = true

        binding.userSubscribe.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onSubscribeChecked(isChecked)
        }
        binding.userRecommendSubscribe.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onRecSubscribeChecked(isChecked)
        }
        binding.userBlock.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onBlockChecked(isChecked)
        }
        binding.userRefresh.setOnRefreshListener {
            lifecycleScope.launch(exceptionHandler) {
                viewModel.fetchUserAndPosts().collect { onFetched() }
            }
        }

        lifecycleScope.launch(exceptionHandler) {
            viewModel.onSubscribe.collect {
                if (it) viewModel.subscribe().await() else viewModel.unsubscribe().await()
            }
            viewModel.onRecSubscribe.collect {
                if (it) viewModel.subscribeRecommendations().await()
                else viewModel.unsubscribeRecommendations().await()
            }
            viewModel.onBlock.collect {
                if (it) viewModel.block().await() else viewModel.unblock().await()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.isActionsVisible.collect { value ->
                binding.userActions.visibility = if (value) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.getUser().collect { user ->
                binding.userToolbar.title = user.formattedLogin
                binding.userName.text = user.name
                binding.userAbout.text = user.about
                binding.userSubscribe.isChecked = user.subscribed == true
                binding.userRecommendSubscribe.isChecked = user.recSubscribed == true
                binding.userBlock.isChecked = user.blocked == true
                viewModel.getAvatar(user.login ?: throw Exception("empty login"))
                        .collect { binding.userAvatar.setImageBitmap(it) }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.getPosts().collect { items -> adapter.list = items }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.fetchUserAndPosts().collect { onFetched() }
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
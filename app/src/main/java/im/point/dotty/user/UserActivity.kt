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
import kotlinx.coroutines.FlowPreview
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

    @FlowPreview
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ViewModelFactory(this,
                intent.getLongExtra(USER_ID, -1), intent.getStringExtra(USER_LOGIN)!!))
                .get(UserViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        adapter = FeedAdapter(lifecycleScope, viewModel::getCommentAvatar)
        adapter.onItemClicked = { item -> startActivity(PostActivity.getIntent(this, PostType.USER_POST, item.id)) }
        binding.userPosts.adapter = adapter
        binding.userPosts.addItemDecoration(RecyclerItemDecorator(this, DividerItemDecoration.VERTICAL, 4))
        binding.userAvatar.outlineProvider = AvatarOutline(64)
        binding.userAvatar.clipToOutline = true

        lifecycleScope.launchWhenStarted {
            binding.userSubscribe.setOnCheckedChangeListener { _, isChecked ->
                launch(exceptionHandler) {
                    viewModel.onSubscribeChecked(isChecked).collect()
                }
            }
            viewModel.isSubscribed.collect { binding.userSubscribe.isChecked = it }
        }
        lifecycleScope.launchWhenStarted {
            binding.userRecommendSubscribe.setOnCheckedChangeListener { _, isChecked ->
                launch(exceptionHandler) {
                    viewModel.onRecSubscribeChecked(isChecked)
                }
            }
            viewModel.isRecSubscribed.collect { binding.userRecommendSubscribe.isChecked = it }
        }
        lifecycleScope.launchWhenStarted {
            binding.userBlock.setOnCheckedChangeListener { _, isChecked ->
                launch(exceptionHandler) {
                    viewModel.onBlockChecked(isChecked)
                }
            }
            viewModel.isBlocked.collect { binding.userBlock.isChecked = it }
        }
        binding.userRefresh.setOnRefreshListener {
            lifecycleScope.launch(exceptionHandler) {
                viewModel.fetchUserAndPosts().collect { onFetched() }
            }
        }

        bind()
    }

    private fun bind() = lifecycleScope.launchWhenStarted {
        launch {
            viewModel.isActionsVisible.collect { value ->
                binding.userActions.visibility = if (value) View.VISIBLE else View.GONE
            }
        }

        launch {
            viewModel.user.collect { user ->
                binding.userToolbar.title = user.formattedLogin
                binding.userName.visibility = if (user.name.isNullOrEmpty()) View.GONE else View.VISIBLE
                binding.userName.text = user.name
                binding.userAbout.visibility = if (user.about.isNullOrEmpty()) View.GONE else View.VISIBLE
                binding.userAbout.text = user.about
            }
        }

        viewModel.getUserAvatar().collect {
            binding.userAvatar.setImageBitmap(it)
        }

        launch {
            viewModel.posts.collect { items -> adapter.list = items }
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
        private const val USER_LOGIN = "user-login"

        fun getIntent(context: Context, userId: Long, login: String): Intent {
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(USER_ID, userId)
            intent.putExtra(USER_LOGIN, login)
            return intent
        }
    }

}
/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */

package im.point.dotty.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.RxActivity
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.ActivityUserBinding
import im.point.dotty.feed.FeedAdapter
import im.point.dotty.model.UserPost

class UserActivity : RxActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: FeedAdapter<UserPost>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ViewModelFactory(this, intent.getLongExtra(USER_ID, -1)))
                .get(UserViewModel::class.java)
        adapter = FeedAdapter()
        binding.userPosts.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        addDisposable(viewModel.isActionsVisible.subscribe { value ->
            binding.userActions.visibility = if (value) View.VISIBLE else View.GONE
        })
        addDisposable(viewModel.getUser().subscribe { user ->
            binding.userName.text = user.name
            binding.userAbout.text = user.about
        })
        binding.userSubscribe.setOnCheckedChangeListener { _, isChecked ->
            addDisposable((if (isChecked) viewModel.unsubscribe() else viewModel.subscribe()).subscribe())
        }
        binding.userRecommendSubscribe.setOnCheckedChangeListener { _, isChecked ->
            addDisposable((if (isChecked) viewModel.unsubscribeRecommendations() else viewModel.subscribeRecommendations()).subscribe())
        }
        binding.userBlock.setOnCheckedChangeListener { _, isChecked ->
            addDisposable((if (isChecked) viewModel.unblock() else viewModel.block()).subscribe())
        }
        binding.userRefresh.setOnRefreshListener {
            addDisposable(viewModel.fetchUserAndPosts().subscribe({ onFetched() },
                    { error -> error.message?.let { showSnackbar(it) } }))
        }
        addDisposable(viewModel.getPosts().subscribe { items -> adapter.list = items })
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
/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */

package im.point.dotty.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import im.point.dotty.common.RxActivity
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.ActivityUserBinding

class UserActivity : RxActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ViewModelFactory(this))
                .get(UserViewModel::class.java)
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
    }

    companion object {
        private const val USER_ID = "user-id"

        fun getIntent(context: Context, userId: String): Intent {
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(USER_ID, userId)
            return intent
        }
    }
}
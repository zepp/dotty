/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.login.LoginViewModel

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelFactory(this))
                .get(LoginViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launchWhenStarted { viewModel.resetActivityBackStack() }
    }
}
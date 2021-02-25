/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import im.point.dotty.domain.AuthViewModel
import im.point.dotty.domain.ViewModelFactory

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelFactory(this))
                .get(AuthViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        viewModel.resetActivityBackStack()
    }
}
/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import im.point.dotty.R
import im.point.dotty.common.NavActivity
import im.point.dotty.common.ViewModelFactory

class MainActivity : NavActivity<MainViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNavGraph(R.navigation.navigation_main)
    }

    override fun provideViewModel() =
            ViewModelProvider(this, ViewModelFactory(this))
                    .get(MainViewModel::class.java)

    companion object {
        fun getIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}
/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */

package im.point.dotty.common

import android.os.Bundle
import androidx.annotation.NavigationRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import im.point.dotty.R

abstract class NavActivity<T : AndroidViewModel> : AppCompatActivity() {
    private lateinit var nav: NavController
    protected val viewModel by lazy { provideViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)
        nav = (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment).navController
    }

    protected fun setNavGraph(@NavigationRes id: Int) = nav.setGraph(id)

    abstract fun provideViewModel(): T
}
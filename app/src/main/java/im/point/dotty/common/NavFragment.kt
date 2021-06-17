/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */

package im.point.dotty.common

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import im.point.dotty.R

abstract class NavFragment<T : AndroidViewModel> : Fragment() {
    protected lateinit var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = provideViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Toolbar>(R.id.toolbar)?.let {
            (activity as AppCompatActivity).setSupportActionBar(it)
            it.setupWithNavController(findNavController(), AppBarConfiguration(findNavController().graph))
            it.setNavigationOnClickListener { findNavController().popBackStack() }
        }
    }

    open fun provideViewModel(): T = (activity as NavActivity<T>).provideViewModel()
}
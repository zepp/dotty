/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import im.point.dotty.R
import im.point.dotty.common.RxActivity
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : RxActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ViewModelFactory<Any>(this))
                .get(MainViewModel::class.java)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        binding.mainTabLayout.setupWithViewPager(binding.mainPager)
        binding.mainPager.adapter = Adapter(supportFragmentManager)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchUnreadCounters().collect()
        }
        addDisposable(viewModel.unreadPosts()
                .subscribe { value -> binding.mainUnreadPosts.text = value.toString() })
        addDisposable(viewModel.unreadComments()
                .subscribe { value -> binding.mainUnreadComments.text = value.toString() })
        addDisposable(viewModel.unreadPrivatePosts()
                .subscribe { value -> binding.mainPrivateUnreadPosts.text = value.toString() })
        addDisposable(viewModel.unreadPrivateComments()
                .subscribe { value -> binding.mainPrivateUnreadComments.text = value.toString() })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.main_logout) {
            lifecycleScope.launch(exceptionHandler) {
                viewModel.logout()
            }
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    private inner class Adapter internal constructor(fm: FragmentManager?) : FragmentStatePagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> RecentFragment()
                1 -> CommentedFragment()
                else -> AllFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.main_recent)
                1 -> getString(R.string.main_commented)
                else -> getString(R.string.main_all)
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }

    companion object {
        fun getIntent(context: Context?): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
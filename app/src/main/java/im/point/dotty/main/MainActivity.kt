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
import im.point.dotty.R
import im.point.dotty.common.RxActivity
import im.point.dotty.databinding.ActivityMainBinding
import im.point.dotty.domain.AuthViewModel
import im.point.dotty.domain.ViewModelFactory

class MainActivity : RxActivity() {
    protected lateinit var binding: ActivityMainBinding
    protected lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this,
                ViewModelFactory(this)).get(AuthViewModel::class.java)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        binding.mainTabLayout.setupWithViewPager(binding.mainPager)
        binding.mainPager.adapter = Adapter(supportFragmentManager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.main_logout) {
            addDisposable(viewModel.logout()
                    .subscribe({ reply -> viewModel.resetActivityBackStack() },
                            { error ->
                                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                                viewModel.resetActivityBackStack()
                            }))
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
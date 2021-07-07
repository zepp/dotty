package im.point.dotty.main

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import im.point.dotty.R
import im.point.dotty.common.NavFragment
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.FragmentMainBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainFragment : NavFragment<MainViewModel>() {
    private lateinit var binding: FragmentMainBinding
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(this::class.simpleName, exception.message, exception)
        Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
    }

    override fun provideViewModel(): MainViewModel =
            ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity()))
                    .get(MainViewModel::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        binding.mainTabLayout.setupWithViewPager(binding.mainPager)
        binding.mainPager.adapter = Adapter(childFragmentManager)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    fun bind() {
        lifecycleScope.launchWhenStarted {
            viewModel.user.collect {
                binding.toolbar.title = it.formattedLogin
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.unreadPosts()
                    .collect { binding.mainUnreadPosts.text = it.toString() }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.unreadComments()
                    .collect { binding.mainUnreadComments.text = it.toString() }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.unreadPrivatePosts()
                    .collect { binding.mainPrivateUnreadPosts.text = it.toString() }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.unreadPrivateComments()
                    .collect { binding.mainPrivateUnreadComments.text = it.toString() }
        }

        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchUnreadCounters().await()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.main_logout) {
            lifecycleScope.launch(exceptionHandler) {
                viewModel.logout().await()
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
}
package im.point.dotty.main

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import im.point.dotty.R
import im.point.dotty.common.NavFragment
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.FragmentMainBinding
import im.point.dotty.user.UserFragment.Companion.USER_ID
import im.point.dotty.user.UserFragment.Companion.USER_LOGIN
import kotlinx.coroutines.CoroutineExceptionHandler
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
        binding.mainPager.adapter = Adapter()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        TabLayoutMediator(binding.mainTabLayout, binding.mainPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.main_recent)
                1 -> getString(R.string.main_commented)
                else -> getString(R.string.main_all)
            }
        }.attach()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    fun bind() {
        lifecycleScope.launch(exceptionHandler) {
            viewModel.fetchUnreadCounters().await()
        }
        binding.toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_user,
                    with(viewModel.user.value) {
                        bundleOf(USER_ID to id, USER_LOGIN to login)
                    })
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

    private inner class Adapter : FragmentStateAdapter(childFragmentManager, lifecycle) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> RecentFragment()
                1 -> CommentedFragment()
                else -> AllFragment()
            }
        }

        override fun getItemCount() = 3
    }
}
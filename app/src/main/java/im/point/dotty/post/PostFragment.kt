package im.point.dotty.post

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.point.dotty.databinding.PostFragmentBinding
import im.point.dotty.domain.PostViewModel
import im.point.dotty.domain.ViewModelFactory

class PostFragment : Fragment() {
    private lateinit var binding: PostFragmentBinding

    companion object {
        const val postId = "post-id"

        fun newInstance(id : String) : PostFragment {
            val fragment = PostFragment()
            val args = Bundle()
            args.putString(postId, id)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: PostViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = PostFragmentBinding.inflate(layoutInflater, container, false);
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelFactory(requireActivity()))
                .get(PostViewModel::class.java)
    }

}
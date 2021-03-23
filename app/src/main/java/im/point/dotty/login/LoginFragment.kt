/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.FragmentLoginBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory<Any>(requireActivity()))
                .get(LoginViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.loginUserName.setText(viewModel.login)
        binding.loginUserName.addTextChangedListener({ s: CharSequence?, i: Int, i1: Int, i2: Int -> },
                { s: CharSequence?, i: Int, i1: Int, i2: Int -> },
                { e -> viewModel.login = e.toString() })
        binding.loginPassword.setText(viewModel.password)
        binding.loginPassword.addTextChangedListener({ s: CharSequence?, i: Int, i1: Int, i2: Int -> },
                { s: CharSequence?, i: Int, i1: Int, i2: Int -> },
                { e -> viewModel.password = e.toString() })
        binding.loginLogin.setOnClickListener {
            lifecycleScope.launch(exceptionHandler) {
                binding.loginLogin.isEnabled = false
                viewModel.login().collect()
            }
        }
        lifecycleScope.launch(exceptionHandler) {
            viewModel.isLoginEnabled.consumeEach { value -> binding.loginLogin.isEnabled = value }
        }
    }

    companion object {
        fun netInstance(): Fragment {
            return LoginFragment()
        }
    }
}
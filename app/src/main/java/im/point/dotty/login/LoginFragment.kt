/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.login

import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(this::class.simpleName, exception.message, exception)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginUserName.addTextChangedListener({ _, _, _, _ -> }, { _, _, _, _ -> },
                { e -> viewModel.login.value = e.toString() })
        binding.loginPassword.addTextChangedListener({ _, _, _, _ -> }, { _, _, _, _ -> },
                { e -> viewModel.password.value = e.toString() })
        binding.loginLogin.setOnClickListener {
            lifecycleScope.launch(exceptionHandler) {
                binding.loginLogin.isEnabled = false
                try {
                    viewModel.login().await()
                } finally {
                    binding.loginLogin.isEnabled = viewModel.isLoginEnabled.value
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            binding.loginUserName.setText(viewModel.login.value)
            binding.loginPassword.setText(viewModel.password.value)
            viewModel.isLoginEnabled.collect {
                binding.loginLogin.isEnabled = it
            }
        }
    }

    companion object {
        fun netInstance(): Fragment {
            return LoginFragment()
        }
    }
}
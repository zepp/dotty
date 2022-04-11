/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.NavFragment
import im.point.dotty.common.onTextChanged
import im.point.dotty.common.repeatOnStarted
import im.point.dotty.common.replaceText
import im.point.dotty.databinding.FragmentLoginBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(InternalCoroutinesApi::class, FlowPreview::class, ExperimentalCoroutinesApi::class)
class LoginFragment : NavFragment<LoginViewModel>() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            binding.loginUserName.replaceText(viewModel.login.value)
            binding.loginPassword.replaceText(viewModel.password.value)
        }
        repeatOnStarted {
            binding.loginUserName.onTextChanged(50)
                    .onEach { text ->
                        with(binding.loginUserName) {
                            if (isFocused) {
                                error = if (text.isEmpty()) {
                                    getString(R.string.login_empty_login)
                                } else {
                                    null
                                }
                            }
                        }
                    }.collect(viewModel.login)
        }
        repeatOnStarted {
            binding.loginPassword.onTextChanged(50)
                    .onEach { text ->
                        with(binding.loginPassword) {
                            if (isFocused) {
                                error = if (text.isEmpty()) {
                                    getString(R.string.login_empty_password)
                                } else {
                                    null
                                }
                            }
                        }
                    }.collect(viewModel.password)
        }
        binding.loginLogin.setOnClickListener {
            lifecycleScope.launch {
                lockUIshowProgress(true)
                viewModel.login().catch { error ->
                    error.message?.let { showSnackbar(it) }
                    Log.e(this::class.simpleName, "error: ", error)
                    lockUIshowProgress(false)
                }.onCompletion {
                    lockUIshowProgress(false)
                }.collect()
            }
        }
    }

    private fun lockUIshowProgress(lock: Boolean) = with(binding) {
        loginProgress.visibility = if (lock) View.VISIBLE else View.GONE
        loginUserName.isEnabled = !lock
        loginPassword.isEnabled = !lock
        loginLogin.isEnabled = if (lock) false else this@LoginFragment.viewModel.isLoginEnabled.value
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(requireActivity().findViewById(R.id.login_layout), text, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        fun netInstance(): Fragment {
            return LoginFragment()
        }
    }
}
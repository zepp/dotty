/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import im.point.dotty.R
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.common.setTextAndCursor
import im.point.dotty.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

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
            lifecycleScope.launch {
                lockUIshowProgress(true)
                viewModel.login().catch { error ->
                    error.message?.let { showSnackbar(it) }
                    Log.e(this::class.simpleName, "error: ", error)
                    lockUIshowProgress(false)
                }.collect {
                    lockUIshowProgress(false)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            binding.loginUserName.setTextAndCursor(viewModel.login.value)
            binding.loginPassword.setTextAndCursor(viewModel.password.value)
            viewModel.isLoginEnabled.collect {
                binding.loginLogin.isEnabled = it
            }
        }
    }

    private fun lockUIshowProgress(lock: Boolean) {
        binding.loginProgress.visibility = if (lock) View.VISIBLE else View.GONE
        binding.loginUserName.isEnabled = !lock
        binding.loginPassword.isEnabled = !lock
        binding.loginLogin.isEnabled = if (lock) false else viewModel.isLoginEnabled.value
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
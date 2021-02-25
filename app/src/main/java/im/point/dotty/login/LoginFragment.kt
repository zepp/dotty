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
import im.point.dotty.common.RxFragment
import im.point.dotty.common.ViewModelFactory
import im.point.dotty.databinding.FragmentLoginBinding

class LoginFragment : RxFragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity()))
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
        addDisposable(viewModel.isLoginEnabled.subscribe { value -> binding.loginLogin.isEnabled = value })
        binding.loginLogin.setOnClickListener {
            addDisposable(viewModel.login()
                    .subscribe { _, error ->
                        Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                    })
        }
    }

    companion object {
        fun netInstance(): Fragment {
            return LoginFragment()
        }
    }
}
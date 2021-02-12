package im.point.dotty.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import im.point.dotty.R
import im.point.dotty.common.RxFragment
import im.point.dotty.databinding.FragmentLoginBinding
import im.point.dotty.domain.AuthViewModel
import im.point.dotty.domain.ViewModelFactory
import im.point.dotty.network.LoginReply
import io.reactivex.observers.DisposableSingleObserver

class LoginFragment : RxFragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel
    private var userNameText = ""
    private var passwordText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,
                ViewModelFactory(requireActivity())).get(AuthViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.loginUserName.addTextChangedListener({ s: CharSequence?, i: Int, i1: Int, i2: Int -> },
                { s: CharSequence?, i: Int, i1: Int, i2: Int -> },
                { e -> userNameText = e.toString().trim { it <= ' ' }
                    updateLogin()})
        binding.loginPassword.addTextChangedListener({ s: CharSequence?, i: Int, i1: Int, i2: Int -> },
                { s: CharSequence?, i: Int, i1: Int, i2: Int -> },
                { e -> passwordText = e.toString().trim { it <= ' ' }
                    updateLogin()})
        binding.loginLogin.setOnClickListener { v ->
            v.isEnabled = false
            addDisposable(viewModel.login(userNameText, passwordText)
                    .subscribe({reply -> viewModel.resetActivityBackStack()},
                            {error ->
                                v.isEnabled = true
                                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()}))
        }
        updateLogin()
    }

    private fun updateLogin() {
        binding.loginLogin.isEnabled = binding.loginUserName.length() > 0 && binding.loginPassword.length() > 0
    }

    companion object {
        fun netInstance(): Fragment {
            return LoginFragment()
        }
    }
}
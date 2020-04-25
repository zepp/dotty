package im.point.dotty.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import im.point.dotty.DottyApplication;
import im.point.dotty.R;
import im.point.dotty.common.RxFragment;
import im.point.dotty.domain.AuthViewModel;
import im.point.dotty.domain.ViewModelFactory;
import im.point.dotty.network.LoginReply;
import io.reactivex.observers.DisposableSingleObserver;


public class LoginFragment extends RxFragment {
    private TextInputEditText userName;
    private TextInputEditText password;
    private Button login;
    private AuthViewModel viewModel;

    private String userNameText = "";
    private String passwordText = "";

    public LoginFragment() {
    }

    public static Fragment netInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this,
                new ViewModelFactory(requireActivity().getApplication())).get(AuthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userName = view.findViewById(R.id.login_user_name);
        password = view.findViewById(R.id.login_password);
        login = view.findViewById(R.id.login_login);
    }

    @Override
    public void onStart() {
        super.onStart();
        userName.addTextChangedListener(new NameTextWatcher());
        password.addTextChangedListener(new PasswordTextWatcher());
        login.setOnClickListener(new OnLogin());
        updateLogin();
    }

    private void updateLogin() {
        login.setEnabled(userName.length() > 0 && password.length() > 0);
    }

    private class OnLogin implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            addDisposable(viewModel.login(userNameText, passwordText)
                    .subscribeWith(new DisposableSingleObserver<LoginReply>() {
                        @Override
                        public void onSuccess(LoginReply loginReply) {
                            DottyApplication.resetActivityBackStack(getActivity().getApplicationContext());
                        }

                        @Override
                        public void onError(Throwable e) {
                            v.setEnabled(true);
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }
    }

    private class NameTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            userNameText = s.toString().trim();
            updateLogin();
        }
    }

    private class PasswordTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            passwordText = s.toString().trim();
            updateLogin();
        }
    }
}

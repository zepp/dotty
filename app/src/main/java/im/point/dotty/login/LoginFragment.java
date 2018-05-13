package im.point.dotty.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import im.point.dotty.R;
import im.point.dotty.domain.AuthInteractor;
import im.point.dotty.network.LoginReply;
import io.reactivex.observers.DisposableSingleObserver;


public class LoginFragment extends Fragment {
    private TextInputEditText userName;
    private TextInputEditText password;
    private Button login;
    private AuthInteractor interactor;

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
        interactor = AuthInteractor.getInstance(getContext());
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
            interactor.login(userNameText, passwordText)
                    .subscribe(new DisposableSingleObserver<LoginReply>() {
                        @Override
                        public void onSuccess(LoginReply loginReply) {
                            if (loginReply.getError() == null) {
                                AuthInteractor.resetActivityBackStack(getContext());
                            } else {
                                Toast.makeText(getContext(), loginReply.getError(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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

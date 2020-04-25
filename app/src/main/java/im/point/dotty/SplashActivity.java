package im.point.dotty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import im.point.dotty.domain.AuthViewModel;
import im.point.dotty.domain.ViewModelFactory;

public class SplashActivity extends AppCompatActivity {
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(AuthViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.resetActivityBackStack();
    }
}

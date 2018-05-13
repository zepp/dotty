package im.point.dotty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import im.point.dotty.domain.AuthInteractor;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthInteractor.resetActivityBackStack(getApplicationContext());
    }
}

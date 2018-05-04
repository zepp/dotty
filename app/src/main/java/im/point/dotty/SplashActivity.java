package im.point.dotty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import im.point.dotty.domain.Controller;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Controller.getInstance(getApplicationContext()).resetActivityBackStack();
    }
}

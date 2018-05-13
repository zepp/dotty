package im.point.dotty;

import android.app.Application;

import im.point.dotty.domain.AuthController;

public final class DottyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AuthController.getInstance(getApplicationContext());
    }
}

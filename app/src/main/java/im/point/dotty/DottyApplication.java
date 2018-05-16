package im.point.dotty;

import android.app.Application;

import im.point.dotty.domain.AuthInteractor;

public final class DottyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AuthInteractor.getInstance(getApplicationContext());
    }
}

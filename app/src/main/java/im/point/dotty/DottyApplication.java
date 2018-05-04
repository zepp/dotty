package im.point.dotty;

import android.app.Application;

import im.point.dotty.domain.Controller;

public final class DottyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Controller.getInstance(getApplicationContext());
    }
}

package im.point.dotty;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import im.point.dotty.domain.AppState;
import im.point.dotty.domain.AuthInteractor;
import im.point.dotty.domain.InteractorManager;
import im.point.dotty.login.LoginActivity;
import im.point.dotty.main.MainActivity;

public final class DottyApplication extends Application {

    public static void resetActivityBackStack(Context context) {
        final int flags = Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
        Intent intent;

        if (AppState.getInstance(context).isLoggedIn()) {
            intent = MainActivity.getIntent(context);
        } else {
            intent = LoginActivity.getIntent(context);
        }
        intent.setFlags(flags);
        context.startActivity(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        InteractorManager.getInstance(getApplicationContext());
    }
}

package im.point.dotty.domain;

import android.content.Context;
import android.content.Intent;

import im.point.dotty.MainActivity;

public final class Controller {
    private static volatile Controller controller;
    private final Context context;
    private final AppState state;

    private Controller(Context context) {
        this.context = context;
        this.state = AppState.getInstance(context);
    }

    public static Controller getInstance(Context context) {
        if (controller == null) {
            synchronized (Controller.class) {
                if (controller == null) {
                    controller = new Controller(context);
                }
            }
        }
        return controller;
    }

    public void resetActivityBackStack() {
        final int flags = Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
        Intent intent;

        if (state.isLoggedIn()) {
            intent = MainActivity.getIntent(context);
        } else {
            intent = MainActivity.getIntent(context);
        }
        intent.setFlags(flags);
        context.startActivity(intent);
    }
}

package im.point.dotty.domain;

import android.content.Context;

public abstract class Interactor {
    protected Context context;

    public void onCreate(Context applicationContext) {
        this.context = applicationContext;
    }

    public void onDestroy() {
    }
}

package im.point.dotty.domain;

import android.content.Context;

public abstract class Interactor {
    public abstract void onCreate(Context applicationContext);
    public abstract void onDestroy();
}

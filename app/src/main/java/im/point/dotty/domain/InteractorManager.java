package im.point.dotty.domain;

import android.content.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class InteractorManager {
    private static volatile InteractorManager manager;
    private final Context applicationContext;
    private final Map<Class<? extends Interactor>,Interactor> items = Collections.synchronizedMap(new HashMap<>());

    private InteractorManager(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static InteractorManager getInstance(Context applicationContext) {
        if (manager == null) {
            synchronized (InteractorManager.class) {
                if (manager == null) {
                    manager = new InteractorManager(applicationContext);
                }
            }
        }
        return manager;
    }

    public <T extends Interactor> T get(Class<T> type) {
        T interactor = (T)items.get(type);
        if (interactor == null) {
            try {
                interactor = type.newInstance();
            } catch (IllegalAccessException e) {
                return null;
            } catch (InstantiationException e) {
                return null;
            }
            interactor.onCreate(applicationContext);
            items.put(type, interactor);
        }
        return interactor;
    }

    public void clear() {
        for (Interactor interactor : items.values()) {
            interactor.onDestroy();
        }
        items.clear();
    }
}

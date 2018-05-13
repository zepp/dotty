package im.point.dotty.domain;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import im.point.dotty.login.LoginActivity;
import im.point.dotty.main.MainActivity;
import im.point.dotty.network.LoginReply;
import im.point.dotty.network.LogoutReply;
import im.point.dotty.network.AuthAPI;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class AuthController {
    private static volatile AuthController controller;
    private final Context context;
    private final AppState state;
    private final Gson gson;
    private final Retrofit retrofit;
    private final AuthAPI api;

    private AuthController(Context context) {
        this.context = context;
        this.state = AppState.getInstance(context);
        this.gson = new GsonBuilder().setLenient().create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(AuthAPI.BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        this.api = retrofit.create(AuthAPI.class);
    }

    public static AuthController getInstance(Context context) {
        if (controller == null) {
            synchronized (AuthController.class) {
                if (controller == null) {
                    controller = new AuthController(context);
                }
            }
        }
        return controller;
    }

    public Observable<LoginReply> login(final String name, String password) {
        Observable<LoginReply> result = api.login(name, password)
                .subscribeOn(Schedulers.io());
        result.subscribe(new DisposableObserver<LoginReply>() {
            @Override
            public void onNext(LoginReply loginReply) {
                if (loginReply.getError() == null) {
                    state.setIsLoggedIn(true);
                    state.setUserName(name);
                    state.setCsrfToken(loginReply.getCsrfToken());
                    state.setToken(loginReply.getToken());
                    resetActivityBackStack();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
        return result.observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<LogoutReply> logout() {
        Observable<LogoutReply> result = api.logout(state.getCsrfToken())
                .subscribeOn(Schedulers.io());
        result.subscribe(new DisposableObserver<LogoutReply>() {
            @Override
            public void onNext(LogoutReply logoutReply) {
                state.setIsLoggedIn(false);
                state.setCsrfToken(null);
                state.setToken(null);
                resetActivityBackStack();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
        return result.observeOn(AndroidSchedulers.mainThread());
    }

    public void resetActivityBackStack() {
        final int flags = Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
        Intent intent;

        if (state.isLoggedIn()) {
            intent = MainActivity.getIntent(context);
        } else {
            intent = LoginActivity.getIntent(context);
        }
        intent.setFlags(flags);
        context.startActivity(intent);
    }
}

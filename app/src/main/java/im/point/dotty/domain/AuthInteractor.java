package im.point.dotty.domain;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import im.point.dotty.login.LoginActivity;
import im.point.dotty.main.MainActivity;
import im.point.dotty.network.AuthAPI;
import im.point.dotty.network.LoginReply;
import im.point.dotty.network.LogoutReply;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class AuthInteractor {
    private static volatile AuthInteractor interactor;
    private final AppState state;
    private final Gson gson;
    private final Retrofit retrofit;
    private final AuthAPI api;

    private AuthInteractor(Context context) {
        this.state = AppState.getInstance(context);
        this.gson = new GsonBuilder().setLenient().create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(AuthAPI.BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.api = retrofit.create(AuthAPI.class);
    }

    public static AuthInteractor getInstance(Context context) {
        if (interactor == null) {
            synchronized (AuthInteractor.class) {
                if (interactor == null) {
                    interactor = new AuthInteractor(context);
                }
            }
        }
        return interactor;
    }

    public Single<LoginReply> login(String name, String password) {
        Single<LoginReply> single = Single.create(emitter -> {
            api.login(name, password).enqueue(new SingleCallbackAdapter<>(emitter));
        });
        single.subscribe(new DisposableSingleObserver<LoginReply>() {
            @Override
            public void onSuccess(LoginReply reply) {
                if (reply.getError() == null) {
                    state.setIsLoggedIn(true);
                    state.setUserName(name);
                    state.setCsrfToken(reply.getCsrfToken());
                    state.setToken(reply.getToken());
                }
            }

            @Override
            public void onError(Throwable e) {
            }
        });
        return single.observeOn(AndroidSchedulers.mainThread());
    }

    public Single<LogoutReply> logout() {
        Single<LogoutReply> single = Single.create(emitter -> {
            api.logout(state.getCsrfToken()).enqueue(new SingleCallbackAdapter<>(emitter));
        });
        single.subscribe(new DisposableSingleObserver<LogoutReply>() {
            @Override
            public void onSuccess(LogoutReply logoutReply) {
                state.setIsLoggedIn(false);
                state.setCsrfToken(null);
                state.setToken(null);
            }

            @Override
            public void onError(Throwable e) {
            }
        });
        return single.observeOn(AndroidSchedulers.mainThread());
    }

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
}

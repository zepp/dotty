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
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class AuthController {
    private static volatile AuthController controller;
    private final AppState state;
    private final Gson gson;
    private final Retrofit retrofit;
    private final AuthAPI api;

    private AuthController(Context context) {
        this.state = AppState.getInstance(context);
        this.gson = new GsonBuilder().setLenient().create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(AuthAPI.BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
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

    public Single<LoginReply> login(String name, String password) {
        Single<LoginReply> single = Single.create(emitter -> {
            api.login(name, password).enqueue(new Callback<LoginReply>() {
                @Override
                public void onResponse(Call<LoginReply> call, Response<LoginReply> response) {
                    LoginReply reply = response.body();
                    if (reply.getError() == null) {
                        state.setIsLoggedIn(true);
                        state.setUserName(name);
                        state.setCsrfToken(reply.getCsrfToken());
                        state.setToken(reply.getToken());
                    }
                    emitter.onSuccess(reply);
                }

                @Override
                public void onFailure(Call<LoginReply> call, Throwable t) {
                    emitter.onError(t);
                }
            });
        });
        return single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<LogoutReply> logout() {
        Single<LogoutReply> single = Single.create(emitter -> {
            api.logout(state.getCsrfToken()).enqueue(new Callback<LogoutReply>() {
                @Override
                public void onResponse(Call<LogoutReply> call, Response<LogoutReply> response) {
                    state.setIsLoggedIn(false);
                    state.setCsrfToken(null);
                    state.setToken(null);
                    emitter.onSuccess(response.body());
                }

                @Override
                public void onFailure(Call<LogoutReply> call, Throwable t) {
                    emitter.onError(t);
                }
            });
        });
        return single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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

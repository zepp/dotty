package im.point.dotty.domain;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import im.point.dotty.network.AuthAPI;
import im.point.dotty.network.LoginReply;
import im.point.dotty.network.LogoutReply;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class AuthInteractor extends Interactor {
    private AppState state;
    private final Gson gson;
    private final Retrofit retrofit;
    private final AuthAPI api;

    private AuthInteractor() {
        this.gson = new GsonBuilder().setLenient().create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(AuthAPI.BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.api = retrofit.create(AuthAPI.class);
    }

    @Override
    public void onCreate(Context applicationContext) {
        this.state = AppState.getInstance(applicationContext);
    }

    public Single<LoginReply> login(String name, String password) {
        Single<LoginReply> single = Single.create(emitter -> {
            api.login(name, password).enqueue(new SingleCallbackAdapter<>(emitter));
        });
        single.subscribe(new DisposableSingleObserver<LoginReply>() {
            @Override
            public void onSuccess(LoginReply reply) {
                state.setIsLoggedIn(true);
                state.setUserName(name);
                state.setCsrfToken(reply.getCsrfToken());
                state.setToken(reply.getToken());
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
}

package im.point.dotty.domain;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import im.point.dotty.login.LoginActivity;
import im.point.dotty.main.MainActivity;
import im.point.dotty.network.AuthAPI;
import im.point.dotty.network.LoginReply;
import im.point.dotty.network.LogoutReply;
import im.point.dotty.network.SingleCallbackAdapter;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class AuthViewModel extends AndroidViewModel {
    private final static String BASE = "https://point.im";
    private final AppState state;
    private final Gson gson;
    private final Retrofit retrofit;
    private final AuthAPI api;

    AuthViewModel(@NonNull Application application) {
        super(application);
        this.gson = new GsonBuilder().setLenient().create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.api = retrofit.create(AuthAPI.class);
        this.state = AppState.getInstance(application);
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
                state.setCsrfToken(reply.csrfToken);
                state.setToken(reply.token);
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

    public void resetActivityBackStack() {
        int flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
        Intent intent;

        if (state.isLoggedIn()) {
            intent = MainActivity.getIntent(getApplication().getBaseContext());
        } else {
            intent = LoginActivity.getIntent(getApplication().getBaseContext());
        }
        intent.setFlags(flags);
        getApplication().startActivity(intent);
    }
}

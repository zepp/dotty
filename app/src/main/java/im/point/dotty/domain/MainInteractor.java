package im.point.dotty.domain;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import im.point.dotty.network.MetaPost;
import im.point.dotty.network.PointAPI;
import im.point.dotty.network.PostsReply;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class MainInteractor extends Interactor {
    private AppState state;
    private final Gson gson;
    private final Retrofit retrofit;
    private final PointAPI api;

    private MainInteractor() {
        this.gson = new GsonBuilder().setLenient().create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(PointAPI.BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.api = retrofit.create(PointAPI.class);
    }

    @Override
    public void onCreate(Context applicationContext) {
        this.state = AppState.getInstance(applicationContext);
    }

    public Single<List<MetaPost>> getRecent() {
        Single<PostsReply> single = Single.create(emitter -> {
            api.getRecent(state.getToken(), null).enqueue(new SingleCallbackAdapter<>(emitter));
        });
        return single.map(reply -> reply.getPosts()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<MetaPost>> getAll() {
        Single<PostsReply> single = Single.create(emitter -> {
            api.getAll(state.getToken(), null).enqueue(new SingleCallbackAdapter<>(emitter));
        });
        return single.map(reply -> reply.getPosts()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<MetaPost>> getCommented() {
        Single<PostsReply> single = Single.create(emitter -> {
           api.getComments(state.getToken(), null).enqueue(new SingleCallbackAdapter<>(emitter));
        });
        return single.map(reply -> reply.getPosts()).observeOn(AndroidSchedulers.mainThread());
    }
}

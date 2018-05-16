package im.point.dotty.domain;

import io.reactivex.SingleEmitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleCallbackAdapter<T> implements Callback<T> {
    private final SingleEmitter<T> emitter;

    SingleCallbackAdapter(SingleEmitter<T> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!response.isSuccessful()) {
            emitter.onError(new RuntimeException("request failed with error code: " + response.code()));
        }
        String contentType = response.headers().get("content-type");
        if (!contentType.equals("application/json")) {
            emitter.onError(new RuntimeException("content type is unsupported: " + contentType));
        }
        emitter.onSuccess(response.body());
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        emitter.onError(t);
    }
}
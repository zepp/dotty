package im.point.dotty.domain;

import im.point.dotty.network.Envelope;
import io.reactivex.ObservableEmitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ObservableCallBackAdapte<T extends Envelope> implements Callback<T> {
    private final ObservableEmitter<T> emitter;

    public ObservableCallBackAdapte(ObservableEmitter<T> emitter) {
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
        T envelope = response.body();
        if (envelope.getError() != null) {
            emitter.onError(new RuntimeException(envelope.getError()));
        }
        emitter.onNext(envelope);
        emitter.onComplete();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        emitter.onError(t);
    }
}

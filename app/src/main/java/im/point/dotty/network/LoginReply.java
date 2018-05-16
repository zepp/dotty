package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

public final class LoginReply extends Envelope {
    String token;
    @SerializedName("csrf_token") String csrfToken;

    public LoginReply(String token, String csrfToken) {
        this.token = token;
        this.csrfToken = csrfToken;
    }

    public String getToken() {
        return token;
    }

    public String getCsrfToken() {
        return csrfToken;
    }
}

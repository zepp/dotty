package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

public final class LoginReply {
    String error;
    String token;
    @SerializedName("csrf_token") String csrfToken;

    public LoginReply(String token, String csrfToken) {
        this.token = token;
        this.csrfToken = csrfToken;
    }

    public LoginReply(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public String getToken() {
        return token;
    }

    public String getCsrfToken() {
        return csrfToken;
    }
}

package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

public final class LogoutReply {
    @SerializedName("ok") Boolean isOk;

    public LogoutReply(Boolean isOk) {
        this.isOk = isOk;
    }

    public Boolean getOk() {
        return isOk;
    }
}

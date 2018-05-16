package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

public final class LogoutReply {
    @SerializedName("ok") boolean isOk;

    public LogoutReply(boolean isOk) {
        this.isOk = isOk;
    }

    public boolean getOk() {
        return isOk;
    }
}

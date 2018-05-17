package im.point.dotty.network;

import com.google.gson.annotations.SerializedName;

public final class UserReply extends Envelope {
    String about;
    String xmpp;
    String name;
    @SerializedName("deny anonymous") boolean isDenyAnonymous;
    @SerializedName("private") boolean isPrivate;
    @SerializedName("subscribed") boolean isSubscribed;
    String created;
    @SerializedName("bl") boolean isBlackListed;
    Boolean gender;
    @SerializedName("wl") boolean isWhiteListed;
    String birthday;
    long id;
    @SerializedName("rec sub") boolean isSubscribedToRecommendations;
    String avatar;
    String skype;
    String login;
    String icq;
    String homepage;
    String email;
    String location;

    public String getAbout() {
        return about;
    }

    public String getXmpp() {
        return xmpp;
    }

    public String getName() {
        return name;
    }

    public boolean isDenyAnonymous() {
        return isDenyAnonymous;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public String getCreated() {
        return created;
    }

    public boolean isBlackListed() {
        return isBlackListed;
    }

    public Boolean getGender() {
        return gender;
    }

    public boolean isWhiteListed() {
        return isWhiteListed;
    }

    public String getBirthday() {
        return birthday;
    }

    public long getId() {
        return id;
    }

    public boolean isSubscribedToRecommendations() {
        return isSubscribedToRecommendations;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getSkype() {
        return skype;
    }

    public String getLogin() {
        return login;
    }

    public String getIcq() {
        return icq;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }
}

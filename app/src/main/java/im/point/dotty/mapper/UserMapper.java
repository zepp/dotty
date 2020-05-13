package im.point.dotty.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import im.point.dotty.model.Gender;
import im.point.dotty.model.User;
import im.point.dotty.network.RawUser;

public final class UserMapper implements Mapper<User, RawUser> {
    private final static SimpleDateFormat birthFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    // TODO fix nano seconds since actual format is 2014-02-03T08:47:45.109228+00:00
    private final static SimpleDateFormat regFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public User map(RawUser user) {
        User result = new User();
        try {
            result.id = user.id;
            result.login = user.login;
            result.name = user.name;
            result.registrationDate = regFormat.parse(user.created);
            result.birthDate = birthFormat.parse(user.birthdate);
            if (user.gender == null) {
                result.gender = Gender.ROBOT;
            } else {
                if (user.gender.equals(true)) {
                    result.gender = Gender.MALE;
                } else {
                    result.gender = Gender.FEMALE;
                }
            }
            result.about = user.about;
        } catch (ParseException e){
            return null;
        }
        return result;
    }
}

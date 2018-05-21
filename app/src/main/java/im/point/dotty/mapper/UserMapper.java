package im.point.dotty.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import im.point.dotty.model.Gender;
import im.point.dotty.model.User;
import im.point.dotty.network.RawUser;

public final class UserMapper {
    private final static SimpleDateFormat birthFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    // TODO fix nano seconds since actual format is 2014-02-03T08:47:45.109228+00:00
    private final static SimpleDateFormat regFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static User map(RawUser user) {
        User result = new User();
        try {
            result.id = user.getId();
            result.login = user.getLogin();
            result.name = user.getName();
            result.registrationDate = regFormat.parse(user.getCreated());
            result.birthDate = birthFormat.parse(user.getBirthdate());
            if (user.getGender() == null) {
                result.gender = Gender.ROBOT;
            } else {
                if (user.getGender().equals(true)) {
                    result.gender = Gender.MALE;
                } else {
                    result.gender = Gender.FEMALE;
                }
            }
            result.about = user.getAbout();
        } catch (ParseException e){
            return null;
        }
        return result;
    }
}

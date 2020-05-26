package im.point.dotty.mapper

import im.point.dotty.model.Gender
import im.point.dotty.model.User
import im.point.dotty.network.RawUser
import im.point.dotty.network.UserReply
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class UserMapper : Mapper<User, UserReply> {
    private val birthFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    // TODO fix nano seconds since actual format is 2014-02-03T08:47:45.109228+00:00
    private val regFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun map(user: UserReply): User {
        val result = User(user.id ?: throw Exception("User id is not provided"))
        result.login = user.login
        result.name = user.name
        result.registrationDate = regFormat.parse(user.created)
        result.birthDate = birthFormat.parse(user.birthdate)
        result.gender = when(user.gender) {
            null -> Gender.ROBOT
            true -> Gender.MALE
            false -> Gender.FEMALE
        }
        result.about = user.about
        return result
    }
}
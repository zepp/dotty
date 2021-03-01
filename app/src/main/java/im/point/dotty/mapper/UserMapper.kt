/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.Gender
import im.point.dotty.model.User
import im.point.dotty.network.UserReply
import java.text.SimpleDateFormat
import java.util.*

class UserMapper : Mapper<User, UserReply> {
    override fun map(user: UserReply): User {
        return merge(User(user.id ?: throw Exception("User id is not provided")), user)
    }

    override fun merge(model: User, user: UserReply): User {
        model.login = user.login
        model.name = user.name
        model.registrationDate = regFormat.parse(user.created)
        user.birthdate?.let { model.birthDate = birthFormat.parse(it) }
        model.gender = when (user.gender) {
            null -> Gender.ROBOT
            true -> Gender.MALE
            false -> Gender.FEMALE
        }
        model.about = user.about
        return model
    }

    companion object {
        private val birthFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        // TODO fix nano seconds since actual format is 2014-02-03T08:47:45.109228+00:00
        private val regFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    }
}
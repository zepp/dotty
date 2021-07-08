/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.mapper

import im.point.dotty.model.UserTag
import im.point.dotty.network.UserTagEntry

class TagMapper(val userId: Long) : Mapper<UserTag, UserTagEntry> {
    override fun map(entry: UserTagEntry): UserTag {
        return UserTag(userId, entry.tag, entry.count)
    }
}
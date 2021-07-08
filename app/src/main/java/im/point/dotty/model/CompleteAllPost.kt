/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.Embedded
import androidx.room.Relation

data class CompleteAllPost(@Embedded
                           override val metapost: AllPost,
                           @Relation(parentColumn = "id", entityColumn = "id")
                           override val post: Post) : CompletePost<AllPost>()
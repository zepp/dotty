package im.point.dotty.model

import androidx.room.Embedded
import androidx.room.Relation

data class CompleteUserPost(@Embedded
                            override val metapost: UserPost,
                            @Relation(parentColumn = "id", entityColumn = "id")
                            override val post: Post) : CompletePost<UserPost>()

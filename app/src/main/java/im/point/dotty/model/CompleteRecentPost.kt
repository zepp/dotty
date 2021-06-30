package im.point.dotty.model

import androidx.room.Embedded
import androidx.room.Relation

data class CompleteRecentPost(@Embedded
                              override val metapost: RecentPost,
                              @Relation(parentColumn = "id", entityColumn = "id")
                              override val post: Post) : CompletePost<RecentPost>()
package im.point.dotty.model

import androidx.room.Embedded
import androidx.room.Relation

data class CompleteTaggedPost(@Embedded
                              override val metapost: TaggedPost,
                              @Relation(parentColumn = "id", entityColumn = "id")
                              override val post: Post) : CompletePost<TaggedPost>()
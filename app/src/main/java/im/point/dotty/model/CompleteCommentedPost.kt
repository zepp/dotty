package im.point.dotty.model

import androidx.room.Embedded
import androidx.room.Relation

data class CompleteCommentedPost(@Embedded
                                 override val metapost: CommentedPost,
                                 @Relation(parentColumn = "id", entityColumn = "id")
                                 override val post: Post) : CompletePost<CommentedPost>()
/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.model

import androidx.room.Embedded
import androidx.room.Relation

data class CompleteCommentedPost(@Embedded
                                 override val metapost: CommentedPost,
                                 @Relation(parentColumn = "id", entityColumn = "id")
                                 override val post: Post,
                                 @Relation(parentColumn = "comment_id", entityColumn = "id")
                                 override var comment: Comment? = null) : CompletePost<CommentedPost>()
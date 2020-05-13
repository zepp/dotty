package im.point.dotty.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import im.point.dotty.model.Comment;
import im.point.dotty.network.RawComment;

public final class CommentMapper implements Mapper<Comment, RawComment> {
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public Comment map(RawComment comment) {
        Comment result = new Comment();
        try {
            result.postId = comment.postId;
            result.id = comment.id;
            result.parentId = comment.toCommentId;
            result.text = comment.text;
            result.timestamp = format.parse(comment.created);
        } catch (ParseException e) {
            return null;
        }
        return result;
    }
}

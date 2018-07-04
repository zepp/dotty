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
            result.postId = comment.getPostId();
            result.id = comment.getId();
            result.parentId = comment.getToCommentId();
            result.text = comment.getText();
            result.timestamp = format.parse(comment.getCreated());
        } catch (ParseException e) {
            return null;
        }
        return result;
    }
}

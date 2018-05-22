package im.point.dotty.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import im.point.dotty.model.AllPost;
import im.point.dotty.model.CommentedPost;
import im.point.dotty.model.Post;
import im.point.dotty.model.RecentPost;
import im.point.dotty.model.UserPost;
import im.point.dotty.network.MetaPost;

public final class PostMapper {
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static <T extends Post> T map(T result, MetaPost post) {
        try {
            result.id = post.getUid();
            result.textId = post.getPost().getId();
            result.userId = post.getPost().getAuthor().getId();
            result.login = post.getPost().getAuthor().getLogin();
            result.name = post.getPost().getAuthor().getName();
            result.text = post.getPost().getText();
            result.timestamp = format.parse(post.getPost().getCreated());
        } catch (ParseException e) {
            return null;
        }
        return result;
    }

    public static RecentPost mapRecentPost(MetaPost post) {
        return map(new RecentPost(), post);
    }

    public static CommentedPost mapCommentedPost(MetaPost post) {
        return map(new CommentedPost(), post);
    }

    public static AllPost mapAllPost(MetaPost post) {
        return map(new AllPost(), post);
    }

    public static UserPost mapUserPost(MetaPost post) {
        return map(new UserPost(), post);
    }
}

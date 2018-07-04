package im.point.dotty.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import im.point.dotty.model.Post;
import im.point.dotty.network.MetaPost;

class PostMapper {
    protected final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    protected static <T extends Post> T map(T result, MetaPost post) {
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
}

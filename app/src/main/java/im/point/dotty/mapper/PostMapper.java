package im.point.dotty.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import im.point.dotty.model.Post;
import im.point.dotty.network.MetaPost;

class PostMapper {
    protected final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    protected static <T extends Post> T map(T result, MetaPost post) {
        try {
            result.id = post.uid;
            result.textId = post.post.id;
            result.userId = post.post.author.id;
            result.login = post.post.author.login;
            result.name = post.post.author.name;
            result.text = post.post.text;
            result.timestamp = format.parse(post.post.created);
        } catch (ParseException e) {
            return null;
        }
        return result;
    }
}

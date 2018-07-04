package im.point.dotty.mapper;

import im.point.dotty.model.CommentedPost;
import im.point.dotty.network.MetaPost;

public class CommentedPostMapper extends PostMapper implements Mapper<CommentedPost, MetaPost> {
    @Override
    public CommentedPost map(MetaPost entry) {
        return map(new CommentedPost(), entry);
    }
}

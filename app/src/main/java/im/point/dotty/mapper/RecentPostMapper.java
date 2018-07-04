package im.point.dotty.mapper;

import im.point.dotty.model.RecentPost;
import im.point.dotty.network.MetaPost;

public class RecentPostMapper extends PostMapper implements Mapper<RecentPost, MetaPost> {
    @Override
    public RecentPost map(MetaPost entry) {
        return map(new RecentPost(), entry);
    }
}

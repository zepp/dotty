package im.point.dotty.mapper;

import im.point.dotty.model.AllPost;
import im.point.dotty.network.MetaPost;

public class AllPostMapper extends PostMapper implements Mapper<AllPost, MetaPost> {
    @Override
    public AllPost map(MetaPost entry) {
        return map(new AllPost(), entry);
    }
}

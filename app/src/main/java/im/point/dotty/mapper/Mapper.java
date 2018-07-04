package im.point.dotty.mapper;

public interface Mapper<T, R> {
    T map(R entry);
}

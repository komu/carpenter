package fi.evident.carpenter.functions;

@FunctionalInterface
public interface Function3<V1, V2, V3, T> {
    T apply(V1 v1, V2 v2, V3 v3);
}

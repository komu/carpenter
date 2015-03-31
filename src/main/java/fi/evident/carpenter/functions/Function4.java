package fi.evident.carpenter.functions;

@FunctionalInterface
public interface Function4<V1, V2, V3, V4, T> {
    T apply(V1 v1, V2 v2, V3 v3, V4 v4);
}

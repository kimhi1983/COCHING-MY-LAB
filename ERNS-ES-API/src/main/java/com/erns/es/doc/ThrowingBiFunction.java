package com.erns.es.doc;

import java.io.IOException;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R> {
    R apply(T t, U u) throws IOException;
}
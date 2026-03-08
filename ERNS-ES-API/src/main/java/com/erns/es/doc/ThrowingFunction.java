package com.erns.es.doc;

import java.io.IOException;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws IOException;
}

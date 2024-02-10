package com.fluxtion.runtime.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface CollectionHelper {
    @SafeVarargs
    static <E> List<E> listOf(E... elements) {
        if (elements.length == 0) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(Arrays.asList(elements));
        }
    }
}

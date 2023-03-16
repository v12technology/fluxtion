package com.fluxtion.compiler.generation.util;

import java.util.Comparator;

public class ClassHierarchyComparator implements Comparator<Class<?>> {

    private final NaturalOrderComparator<?> naturalOrderComparator;

    public ClassHierarchyComparator(NaturalOrderComparator<?> naturalOrderComparator) {
        this.naturalOrderComparator = naturalOrderComparator;
    }

    @Override
    public int compare(Class<?> c1, Class<?> c2) {
        if (c1 == null) {
            if (c2 == null) {
                return 0;
            } else {
                // Sort nulls first
                return -1;
            }
        } else if (c2 == null) {
            // Sort nulls first
            return 1;
        }

        // At this point, we know that c1 and c2 are not null
        if (c1.equals(c2)) {
            return naturalOrderComparator.compare(c1, c2);
        }

        // At this point, c1 and c2 are not null and not equal, here we
        // compare them to see which is "higher" in the class hierarchy
        boolean c1Lower = c2.isAssignableFrom(c1);
        boolean c2Lower = c1.isAssignableFrom(c2);

        if (c1Lower && !c2Lower) {
            return -1;
        } else if (c2Lower && !c1Lower) {
            return 1;
        }

        // Doesn't matter, sort consistently on classname - using natural order
        return naturalOrderComparator.compare(c1, c2);
    }


}

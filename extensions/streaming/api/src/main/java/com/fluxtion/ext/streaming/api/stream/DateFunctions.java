/*
 * Copyright (C) 2021 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.ext.streaming.api.Stateful;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author V12 Technology Ltd.
 */
public class DateFunctions {

    public static class DateRange implements Stateful {

        private final MinDate minDate = new MinDate();
        private final MaxDate maxDate = new MaxDate();

        public DateRange check(LocalDate val) {
            minDate.check(val);
            maxDate.check(val);
            return this;
        }

        public LocalDate maxDate() {
            return maxDate.max;
        }

        public LocalDate minDate() {
            return minDate.min;
        }

        @Override
        @Initialise
        public void reset() {
            minDate.reset();
            maxDate.reset();
        }

        public Set<Integer> yearMonths() {
            int start = minDate().getYear() * 100 + minDate().getMonth().getValue();
            int end = maxDate().getYear() * 100 + maxDate().getMonth().getValue();
            Set<Integer> set = new LinkedHashSet<>();
            for (int i = start; i < end; i++) {
                int monthCount = i % 100;
                if (monthCount == 0 | monthCount > 12) {
                   continue;
                }
                set.add(i);
            }
            return set;
        }

    }

    public static class MaxDate implements Stateful {

        private LocalDate max = LocalDate.MIN;

        public LocalDate check(LocalDate val) {

            if (max.isBefore(val)) {
                max = val;
            }
            return max;
        }

        @Override
        @Initialise
        public void reset() {
            max = LocalDate.MIN;
        }
    }

    public static class MinDate implements Stateful {

        private LocalDate min = LocalDate.MAX;

        public LocalDate check(LocalDate val) {

            if (val.isBefore(min)) {
                min = val;
            }
            return min;
        }

        @Override
        @Initialise
        public void reset() {
            min = LocalDate.MAX;
        }
    }
}

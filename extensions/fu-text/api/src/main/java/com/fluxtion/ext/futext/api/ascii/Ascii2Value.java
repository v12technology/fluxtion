/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.futext.api.ascii;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.ConfigVariable;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.filter.AsciiMatchFilter;


/**
 *
 * @author Greg Higgins
 */
public abstract class Ascii2Value implements NumericValue {

    /**
     * notifies the converter the search filter can be applied.
     */
    public Object notifier;
    /**
     * the length of chars to be processed by parser when the search filter
     * matches
     */
    public byte length;
    /**
     * internal flag, true means apply filter and parser
     */
    protected boolean applyFilter;
    /**
     * internal flag, set to true when filter matches and ready to add to input
     * buffer for later parsing;
     */
    protected boolean filterMateched;
    /**
     * internal flag, if true the char event should be used for parsing the
     * intValue
     */
    protected boolean processCharForParse;
    /**
     * the number of characters processed by the
     */
    protected byte fieldCharCount;
    /**
     * only apply the filter match when the notifier is observed and notifies.
     * Otherwise ignore the notifier and always apply filter.
     *
     */
    protected transient boolean ignoreNotifier;
    /**
     * the actual intValue
     */
    protected int intValue;

    protected double doubleValue;

    protected long longValue;
    /**
     * sign
     */
    protected int sign;
    /**
     * intermediate intValue mutated while parsing
     */
    protected int intermediateVal;

    public Ascii2Value(Object notifier, byte length, String searchFilter) {
        this.notifier = notifier;
        this.length = length;
        this.searchFilter = searchFilter;
    }

    public Ascii2Value(Object notifier, String searchFilter) {
        this.notifier = notifier;
        this.searchFilter = searchFilter;
    }

    public Ascii2Value() {
    }

    /**
     * Injected ascii search filter
     */
    @Inject
    @ConfigVariable(key = AsciiMatchFilter.KEY_FILTER_STRING, field = "searchFilter")
    public AsciiMatchFilter filter;
    
    public transient String searchFilter;

    @OnParentUpdate(value = "notifier")
    public void applyFilter(Object notifier) {
        applyFilter = true;
    }

    @OnParentUpdate(value = "filter")
    public void filterMatched(AsciiMatchFilter filter) {
        filterMateched = applyFilter;
    }

//    @OnEvent
//    public void onEvent() {
//    }
    @AfterEvent
    public final void afterEvent() {
        processCharForParse = applyFilter & filterMateched;
        intermediateVal = processCharForParse?intermediateVal:0;
    }

    @Override
    public int intValue() {
        return intValue;
    }

    @Override
    public long longValue() {
        return longValue;
    }

    @Override
    public double doubleValue() {
        return doubleValue;
    }

    @Initialise
    public void init() {
        ignoreNotifier = notifier == null;
        applyFilter = ignoreNotifier;
        filterMateched = false;
        processCharForParse = false;
        sign = 1;
    }

    @EventHandler(filterId = '-')
    public boolean onSign(CharEvent e) {
        if (processCharForParse) {
            fieldCharCount++;
            sign = -1;
        }
        return false;
    }

    protected void resetParse() {
        applyFilter = ignoreNotifier;
        filterMateched = false;
        processCharForParse = searchFilter == null;
//        processCharForParse = false;
        fieldCharCount = 0;
        intValue = intermediateVal * sign;
        doubleValue = intValue;
        longValue = intValue;
        intermediateVal = 0;
        sign = 1;
    }

}

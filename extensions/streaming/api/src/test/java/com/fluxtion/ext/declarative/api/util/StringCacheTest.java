package com.fluxtion.ext.declarative.api.util;

import com.fluxtion.ext.streaming.api.util.StringCache;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * 
 * @author gregp
 */
public class StringCacheTest {

    @Test
    public void cacheTest() {
        StringBuilder sb_0 = new StringBuilder("sb_0");
        StringBuilder sb_1 = new StringBuilder("sb_1");
        StringBuilder sb_2 = new StringBuilder("sb_2");
        StringBuilder sb_22 = new StringBuilder("sb_2");

        StringCache cache = new StringCache();
        assertThat(sb_0, is(not(sb_1)));
        assertThat(sb_2, is(not(sb_22)));
        assertThat(sb_2.toString(), is((sb_22.toString())));
        
        cache.intern(sb_0);
        cache.intern(sb_0);
        cache.intern(sb_0);
        assertThat(1, is(cache.cacheSize()));
        
        CharSequence sb_2_String = cache.intern(sb_2);
        CharSequence sb_22_String = cache.intern(sb_22);
        assertThat(2, is(cache.cacheSize()));
        
        Assert.assertTrue(sb_2_String==sb_22_String);
    }
}

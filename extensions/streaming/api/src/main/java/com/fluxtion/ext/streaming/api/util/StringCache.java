package com.fluxtion.ext.streaming.api.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A String cache, receives a {@link CharSequence} and checks if there is a
 * match in the cache, returning a cached char sequence that equals the input.
 * 
 * This is similar functionality to {@link String#intern() } but does not require
 * a String to intern, only a CharSequence.
 *
 * @author gregp
 */
public class StringCache {

    private final ByteBuffer buffer;
    private final byte[] array;
    private static final int DEFAULT_SIZE = 64;
    private final HashMap<ByteBuffer, String> cache;

    public StringCache() {
        array = new byte[DEFAULT_SIZE];
        buffer = ByteBuffer.wrap(array);
        cache = new HashMap<>(256);
    }

    public CharSequence intern(CharSequence cs) {
        buffer.clear();
        for (int i = 0; i < cs.length(); i++) {
            buffer.put((byte) cs.charAt(i));
        }
        buffer.flip();
        String ret = cache.get(buffer);
        if (ret == null) {
            ret = new String(array, 0, buffer.limit());
            cache.put(ByteBuffer.wrap(Arrays.copyOf(array, buffer.limit())), ret);
        }
        return ret;
    }

    public int cacheSize() {
        return cache.size();
    }

    public void clearCache() {
        cache.clear();
    }

}

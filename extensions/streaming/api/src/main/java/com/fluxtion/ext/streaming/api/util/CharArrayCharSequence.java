package com.fluxtion.ext.streaming.api.util;

/**
 * CharSequence backed by an array. Views on the array can be accessed using
 * {@link #subSequence(int, int) } returning a {@link CharSequence} that points
 * to the underlying char array. The view can re-point its view by calling
 * {@link #subSequence(int, int) } this will return the original view but with
 * pointers moved to new positions on the underlying array.
 *
 * @author gregp
 */
public class CharArrayCharSequence implements CharSequence {

    private final char[] array;
    private StringCache cache = new StringCache();

    public CharArrayCharSequence(char[] array) {
        this.array = array;
    }

    @Override
    public char charAt(int index) {
        return array[index];
    }

    @Override
    public int length() {
        return array.length;
    }

    @Override
    public CharSequenceView subSequence(int start, int end) {
        return new CharSequenceView(start, end);
    }

    public CharSequenceView view() {
        return subSequence(0, 0);
    }

    @Override
    public String toString() {
        return new String(array);
    }

    @Override
    public int hashCode() {
        return hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }

    static int hashCode(final CharSequence csq) {
        int h = 0;
        for (int i = 0, len = csq.length(); i < len; i++) {
            h = 31 * h + csq.charAt(i);
        }
        return h;
    }

    public class CharSequenceView implements CharSequence {

        private int start;
        private int end;
        private boolean updated = true;
        private String myString = null;

        public CharSequenceView(int start, int end) {
            this.start = start;
            this.end = end;
            updated = true;
        }

        @Override
        public char charAt(int index) {
            return CharArrayCharSequence.this.charAt(index + start);
        }

        @Override
        public int length() {
            return end - start;
        }

        public CharSequenceView subSequenceNoOffset(int start, int end) {
            updated = true;
            this.start = start;
            this.end = end;
            return this;
        }

        @Override
        public CharSequenceView subSequence(int newStart, int newEnd) {
            updated = true;
            this.start = newStart + start;
            this.end = newEnd + start;
            return this;
        }

        @Override
        public String toString() {
            if(updated){
                myString = new String(array, start, end - start);
            }
            return myString;
        }
        
        public String intern() {
            return (String) cache.intern(this);
        }

        @Override
        public int hashCode() {
            return CharArrayCharSequence.hashCode(CharSequenceView.this);
        }

        @Override
        public boolean equals(Object obj) {
            return toString().equals(obj.toString());
        }

        public final CharSequenceView trim() {
            while ((start < end) && (array[start] <= ' ')) {
                start++;
            }
            while ((start < end) && (array[end - 1] <= ' ')) {
                end--;
            }
            return this;
        }

    }

}

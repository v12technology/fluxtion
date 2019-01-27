package com.fluxtion.ext.declarative.api.util;

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
    
    public CharSequenceView view(){
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

        public CharSequenceView(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public char charAt(int index) {
            return CharArrayCharSequence.this.charAt(index + start);
        }

        @Override
        public int length() {
            return end - start;
        }

        @Override
        public CharSequenceView subSequence(int start, int end) {
            this.start = start;
            this.end = end;
            return this;
        }

        @Override
        public String toString() {
            return new String(array, start, end - start);
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

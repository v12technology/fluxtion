package com.fluxtion.compiler.replay;

import java.io.Writer;

public class NullWriter extends Writer {

    /**
     * A singleton.
     */
    public static final NullWriter NULL_WRITER = new NullWriter();

    /**
     * Constructs a new NullWriter.
     */
    public NullWriter() {
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param c The character to write
     * @return this writer
     * @since 2.0
     */
    @Override
    public Writer append(final char c) {
        //to /dev/null
        return this;
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param csq   The character sequence to write
     * @param start The index of the first character to write
     * @param end   The index of the first character to write (exclusive)
     * @return this writer
     * @since 2.0
     */
    @Override
    public Writer append(final CharSequence csq, final int start, final int end) {
        //to /dev/null
        return this;
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param csq The character sequence to write
     * @return this writer
     * @since 2.0
     */
    @Override
    public Writer append(final CharSequence csq) {
        //to /dev/null
        return this;
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param idx The character to write
     */
    @Override
    public void write(final int idx) {
        //to /dev/null
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param chr The characters to write
     */
    @Override
    public void write(final char[] chr) {
        //to /dev/null
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param chr The characters to write
     * @param st  The start offset
     * @param end The number of characters to write
     */
    @Override
    public void write(final char[] chr, final int st, final int end) {
        //to /dev/null
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param str The string to write
     */
    @Override
    public void write(final String str) {
        //to /dev/null
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param str The string to write
     * @param st  The start offset
     * @param end The number of characters to write
     */
    @Override
    public void write(final String str, final int st, final int end) {
        //to /dev/null
    }

    /**
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() {
        //to /dev/null
    }

    /**
     * @see java.io.Writer#close()
     */
    @Override
    public void close() {
        //to /dev/null
    }

}


package com.fluxtion.compiler.builder.imperative;

import java.io.OutputStream;
import java.io.PrintStream;

public class DoNothingPrintStream extends PrintStream {

    private static final OutputStream support = new OutputStream() {
        public void write(int b) {
        }
    };

    public DoNothingPrintStream() {
        super(support);
        if (support == null)
            System.out.println("DoNothingStream has null support");
    }


}
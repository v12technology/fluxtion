package net.openhft.compiler.eg;

import net.openhft.compiler.eg.components.BarImpl;
import net.openhft.compiler.eg.components.TeeImpl;
import net.openhft.compiler.eg.components.Foo;

public class FooBarTee{
    public final String name;
    public final TeeImpl tee;
    public final BarImpl bar;
    public final BarImpl copy;
    public final Foo foo;

    public FooBarTee(String name) {
        // when viewing this file, ensure it is synchronised with the copy on disk.
        System.out.println("generated test Sat Feb 01 13:09:17 GMT 2020");
        this.name = name;

        tee = new TeeImpl("test");

        bar = new BarImpl(tee, 55);

        copy = new BarImpl(tee, 555);

        // you should see the current date here after synchronisation.
        foo = new Foo(bar, copy, "generated test Sat Feb 01 13:09:17 GMT 2020", 5);
    }

    public void start() {
    }

    public void stop() {
    }

    public void close() {
        stop();

    }
}

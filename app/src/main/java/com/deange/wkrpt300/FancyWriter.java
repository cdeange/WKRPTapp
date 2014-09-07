package com.deange.wkrpt300;

import java.io.PrintStream;
import java.io.PrintWriter;

public final class FancyWriter {

    public static final int ROW_LENGTH = 50;

    final StringBuilder mBuilder;

    public FancyWriter() {
        mBuilder = new StringBuilder();
    }

    public FancyWriter(final StringBuilder out) {
        mBuilder = out;
    }

    public FancyWriter print(Object o) {
        return print(String.valueOf(o));
    }

    public FancyWriter print(String s) {
        append(s);
        return printEmptyLine();
    }

    public FancyWriter print(String s, int repeating) {
        for (int i = 0; i < repeating; i++) {
            append(s);
        }
        return printEmptyLine();
    }

    public FancyWriter printRow(String s) {
        return print(s, ROW_LENGTH);
    }

    public FancyWriter printVar(String name, final Object val) {
        append(name, " = \'", String.valueOf(val), "\'");
        return printEmptyLine();
    }

    public FancyWriter printEmptyLine() {
        return append('\n');
    }

    public void flush() {
        flush(System.out);
    }

    public void flush(final PrintStream ps) {
        ps.println(mBuilder.toString());
        mBuilder.delete(0, mBuilder.length());
    }

    public void flush(final PrintWriter pw) {
        pw.print(mBuilder.toString());
        mBuilder.delete(0, mBuilder.length());
    }

    // HELPERS

    private FancyWriter append(CharSequence... csqs) {
        for (CharSequence csq : csqs) {
            mBuilder.append(csq);
        }
        return this;
    }

    private FancyWriter append(char... cs) {
        for (char c : cs) {
            mBuilder.append(c);
        }
        return this;
    }

}
package com.deange.wkrpt300;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

public class CharArrayPrintWriter extends PrintWriter {

    private final CharArrayWriter mDelegate;

    public CharArrayPrintWriter() {
        super(new CharArrayWriter());
        mDelegate = (CharArrayWriter) out;
    }

    @Override
    public String toString() {
        return mDelegate.toString();
    }

    public void write(char[] cbuf, int off, int len) {
        mDelegate.write(cbuf, off, len);
    }
}
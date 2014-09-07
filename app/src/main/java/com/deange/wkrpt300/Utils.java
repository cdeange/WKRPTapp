package com.deange.wkrpt300;

import android.content.Context;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static byte[] streamToByteArray(final InputStream in) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = in.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return baos.toByteArray();
    }

    public static void streamToStream(final InputStream in, final OutputStream out) {
        try {
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String dumpBundle(final Bundle b) {
        final StringBuilder sb = new StringBuilder();
        for (String key : b.keySet()) {
            sb.append(key).append('=').append(b.get(key)).append('\n');
        }

        return sb.toString();
    }

}

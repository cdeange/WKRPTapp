package com.deange.wkrpt300.network.retrofit;

import com.deange.wkrpt300.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import retrofit.mime.TypedOutput;

class TypedOutputStream implements TypedOutput {

    private final String mFileName;
    private final String mMimeType;
    private final InputStream mStream;

    public TypedOutputStream(String fileName, String mimeType, InputStream stream) {
        mFileName = fileName;
        mMimeType = mimeType;
        mStream = stream;
    }

    @Override
    public String fileName() {
        return mFileName;
    }

    @Override public String mimeType() {
        return mMimeType;
    }

    @Override public long length() {
        return -1;
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException {
        Utils.streamToStream(mStream, out);
    }
}

package com.deange.wkrpt300.network.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.deange.wkrpt300.Utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class PostFileRequest extends StringRequest {

    private final InputStream mStream;

    public PostFileRequest(final String url,
                           final Response.Listener<String> listener,
                           final Response.ErrorListener errorListener,
                           final InputStream stream) {
        super(Method.POST, url, listener, errorListener);
        mStream = stream;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Utils.streamToStream(mStream, out);
        return out.toByteArray();
    }
}

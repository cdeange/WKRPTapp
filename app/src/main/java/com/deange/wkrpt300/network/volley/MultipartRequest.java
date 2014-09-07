package com.deange.wkrpt300.network.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.UUID;

public class MultipartRequest extends Request<String> {

    private final MultipartEntity mEntity;
    private final Response.Listener<String> mListener;

    public MultipartRequest(final String url,
                            final Response.ErrorListener errorListener,
                            final Response.Listener<String> listener) {
        super(Method.POST, url, errorListener);
        final Charset charset = Charset.defaultCharset();
        final String boundary = UUID.randomUUID().toString().replace("-", "");
        mEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, boundary, charset);
        mListener = listener;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            mEntity.writeTo(out);
        } catch (final IOException e) {
            throw new AuthFailureError("Could not write output stream", e);
        }
        return out.toByteArray();
    }

    public void addFilePart(final String name, final InputStream stream) {
        mEntity.addPart(name, new InputStreamBody(stream, name));
    }

    public void addStringPart(final Object name, final Object value) {
        try {
            mEntity.addPart(String.valueOf(name), new StringBody(String.valueOf(value),
                    Charset.defaultCharset()));
        } catch (final UnsupportedEncodingException e) {
            // Should not happen, UTF-8 is always supported
            e.printStackTrace();
        }
    }

    @Override
    public String getBodyContentType() {
        return mEntity.getContentType().getValue();
    }

    @Override
    protected void deliverResponse(String response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return Response.success(new String(response.data), getCacheEntry());
    }
}

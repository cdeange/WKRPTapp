package com.deange.wkrpt300.network.volley;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.deange.wkrpt300.Utils;
import com.deange.wkrpt300.model.Countdown;
import com.deange.wkrpt300.model.OperationController;
import com.deange.wkrpt300.model.OperationParams;
import com.deange.wkrpt300.model.ResponseStats;
import com.deange.wkrpt300.network.NetworkLibrary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class VolleyLibrary implements NetworkLibrary {

    private final Context mContext;
    private final OperationController mController;
    private final RequestQueue mQueue;

    public VolleyLibrary(final Context context) {
        mContext = context;
        mController = new OperationController(context);

        mQueue = Volley.newRequestQueue(mContext);
        mQueue.start();
    }

    @Override
    public ResponseStats.Get get(final String url, final OperationParams.Get params)
            throws IOException {
        mController.reset();
        mController.start();

        final RequestFuture<String> future = RequestFuture.newFuture();
        mQueue.add(new StringRequest(Request.Method.GET, url, future, future));
        final String response = get(future);

        mController.stop();

        return new ResponseStats.Get(mController, response);
    }

    @Override
    public ResponseStats.Post post(final String url, final OperationParams.Post params)
            throws IOException {
        mController.reset();
        mController.start();

        final RequestFuture<String> future = RequestFuture.newFuture();
        mQueue.add(new PostFileRequest(url, future, future,
                mContext.getResources().openRawResource(params.rawResourceId)));
        final String response = get(future);

        mController.stop();

        return new ResponseStats.Post(mController, response);
    }

    @Override
    public ResponseStats.MultipartPost postMultipart(final String url,
                                                     final OperationParams.Multipart params)
            throws IOException {
        mController.reset();
        mController.start();

        final RequestFuture<String> future = RequestFuture.newFuture();
        final MultipartRequest request = new MultipartRequest(url, future, future);
        request.addStringPart(params.formField.first, params.formField.second);
        request.addFilePart(params.fileName,
                mContext.getResources().openRawResource(params.rawResourceId));

        mQueue.add(request);
        final String response = get(future);

        mController.stop();

        return new ResponseStats.MultipartPost(mController, response);
    }

    @Override
    public ResponseStats.ImageGet loadImage(final String url, final OperationParams.Image params)
            throws IOException {
        mController.reset();
        mController.start();

        final RequestFuture<Bitmap> future = RequestFuture.newFuture();
        mQueue.add(new ImageRequest(url, future, 0, 0, Bitmap.Config.ARGB_8888, future));
        final Bitmap response = get(future);

        mController.stop();

        return new ResponseStats.ImageGet(mController, response);
    }

    @Override
    public ResponseStats batchGet(final String... urls) throws IOException {

        mController.reset();
        mController.start();

        final Countdown countdown = new Countdown();

        for (final String url : urls) {
            final CountdownListener<String> listener = new CountdownListener<String>(countdown);
            mQueue.add(new StringRequest(Request.Method.GET, url, listener, listener));
            countdown.await();
        }

        countdown.blockUntilDone();
        mController.stop();

        return new ResponseStats(mController);
    }

    private static <T> T get(final RequestFuture<T> future) throws IOException {
        T response;
        try {
            response = future.get();
        } catch (Exception e) {
            throw new IOException(e);
        }

        return response;
    }

    private static class CountdownListener<T>
            implements Response.Listener<T>, Response.ErrorListener {

        private final Countdown mCountdown;

        private CountdownListener(final Countdown countdown) {
            mCountdown = countdown;
        }

        @Override
        public void onErrorResponse(final VolleyError error) {
            mCountdown.signal();
        }

        @Override
        public void onResponse(final T response) {
            mCountdown.signal();
        }
    }

    private static class PostFileRequest extends StringRequest {

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

}

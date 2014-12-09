package com.deange.wkrpt300.network.volley;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.deange.wkrpt300.model.Countdown;
import com.deange.wkrpt300.model.OperationController;
import com.deange.wkrpt300.model.OperationParams;
import com.deange.wkrpt300.model.ResponseStats;
import com.deange.wkrpt300.network.NetworkLibrary;

import java.io.IOException;

public class VolleyLibrary extends NetworkLibrary {

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
        mCountdown = new Countdown();

        for (final String url : urls) {
            final CountdownListener<String> listener = new CountdownListener<String>(mCountdown);
            mQueue.add(new StringRequest(Request.Method.GET, url, listener, listener));
            mCountdown.await();
        }

        mCountdown.blockUntilDone();
        mController.stop();

        return new ResponseStats(mController);
    }

    private static <T> T get(final RequestFuture<T> future) throws IOException {
        T response;
        try {
            response = future.get();
        } catch (final Exception e) {
            throw new IOException(e);
        }

        return response;
    }

}

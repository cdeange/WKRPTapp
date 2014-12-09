package com.deange.wkrpt300.network.retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.deange.wkrpt300.model.Countdown;
import com.deange.wkrpt300.model.OperationController;
import com.deange.wkrpt300.model.ResponseStats;
import com.deange.wkrpt300.network.LibraryRunner;
import com.deange.wkrpt300.network.NetworkLibrary;
import com.deange.wkrpt300.model.OperationParams;

import java.io.IOException;
import java.util.concurrent.Executor;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.http.Body;
import retrofit.http.EncodedPath;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedOutput;

public class RetrofitLibrary extends NetworkLibrary {

    private interface NetworkAdapter {

        @GET("/{path}")
        public String get(@EncodedPath("path") String relativePath);

        @POST("/post.php?dir=deange")
        public String post(@Body TypedOutput body);

        @Multipart
        @POST("/post.php?dir=deange")
        public String multipartPost(@Part(LibraryRunner.RANDOM_TXT_FILENAME) TypedOutput body,
                                    @Part(LibraryRunner.MULTIPART_KEY) Object value);

        @GET("/{image}")
        public Response getImage(@EncodedPath("image") String relativePath);

        @GET("/{path}")
        public void get(@EncodedPath("path") String relativePath, Callback<String> callback);

    }

    private final Context mContext;
    private final Executor mMainExecutor;
    private final OperationController mController;
    private final NetworkAdapter mDeangeAdapter;
    private final NetworkAdapter mPostTestServerAdapter;

    public RetrofitLibrary(final Context context) {
        mContext = context;
        mController = new OperationController(context);

        final Converter converter = new PlainConverter();
        mMainExecutor = new Executor() {
            final Handler mHandler = new Handler(Looper.getMainLooper());

            @Override
            public void execute(final Runnable runnable) {
                mHandler.post(runnable);
            }
        };

        mDeangeAdapter = new RestAdapter.Builder()
                .setEndpoint("http://deange.ca")
                .setConverter(converter)
                .setExecutors(mMainExecutor, AsyncTask.THREAD_POOL_EXECUTOR)
                .build()
                .create(NetworkAdapter.class);

        mPostTestServerAdapter = new RestAdapter.Builder()
                .setEndpoint("http://posttestserver.com")
                .setConverter(converter)
                .setExecutors(mMainExecutor, AsyncTask.THREAD_POOL_EXECUTOR)
                .build()
                .create(NetworkAdapter.class);
    }

    @Override
    public ResponseStats.Get get(final String url, final OperationParams.Get params)
            throws IOException {
        mController.reset();
        mController.start();

        // It's a one-liner!
        final String response = mPostTestServerAdapter.get(getEndpointFromUrl(url));

        mController.stop();

        return new ResponseStats.Get(mController, response);
    }

    @Override
    public ResponseStats.Post post(final String url, final OperationParams.Post params)
            throws IOException {
        mController.reset();
        mController.start();

        // It's a one-liner!
        final String response = mPostTestServerAdapter.post(
                new TypedOutputStream(params.fileName, "text/plain",
                        mContext.getAssets().open(params.fileName)));

        mController.stop();

        return new ResponseStats.Post(mController, response);
    }

    @Override
    public ResponseStats.MultipartPost postMultipart(final String url,
                                                     final OperationParams.Multipart params)
            throws IOException {
        mController.reset();
        mController.start();

        // It's a one-liner!
        final String response = mPostTestServerAdapter.multipartPost(
                new TypedOutputStream(params.fileName, "text/plain",
                        mContext.getAssets().open(params.fileName)), params.formField.second);

        mController.stop();

        return new ResponseStats.MultipartPost(mController, response);
    }

    @Override
    public ResponseStats.ImageGet loadImage(final String url, final OperationParams.Image params)
            throws IOException {
        mController.reset();
        mController.start();

        // It's a one-liner!
        final Bitmap bitmap = BitmapFactory.decodeStream(
                mDeangeAdapter.getImage(getEndpointFromUrl(url)).getBody().in());

        mController.stop();

        return new ResponseStats.ImageGet(mController, bitmap);
    }

    public ResponseStats batchGet(final String... urls) throws IOException{

        mController.reset();
        mController.start();
        mCountdown = new Countdown();

        final Callback<String> callback = new Callback<String>() {
            @Override
            public void success(final String s, final Response response) {
                mCountdown.signal();
            }

            @Override
            public void failure(final RetrofitError error) {
                mCountdown.signal();
            }
        };

        for (final String url : urls) {
            mPostTestServerAdapter.get(getEndpointFromUrl(url), callback);
            mCountdown.await();
        }

        mCountdown.blockUntilDone();
        mController.stop();

        return new ResponseStats(mController);
    }

    private static String getEndpointFromUrl(final String fullUrl) {
        return Uri.parse(fullUrl).getPath().substring(1);
    }

}

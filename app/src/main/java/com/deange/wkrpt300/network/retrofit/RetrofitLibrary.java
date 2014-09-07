package com.deange.wkrpt300.network.retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.deange.wkrpt300.Utils;
import com.deange.wkrpt300.model.Countdown;
import com.deange.wkrpt300.model.OperationController;
import com.deange.wkrpt300.model.ResponseStats;
import com.deange.wkrpt300.network.LibraryRunner;
import com.deange.wkrpt300.network.NetworkLibrary;
import com.deange.wkrpt300.model.OperationParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

public class RetrofitLibrary implements NetworkLibrary {

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
    private final OperationController mController;
    private final NetworkAdapter mDeangeAdapter;
    private final NetworkAdapter mPostTestServerAdapter;

    public RetrofitLibrary(final Context context) {
        mContext = context;
        mController = new OperationController(context);

        final Converter converter = new PlainConverter();

        mDeangeAdapter = new RestAdapter.Builder()
                .setEndpoint("http://deange.ca")
                .setConverter(converter)
                .build()
                .create(NetworkAdapter.class);

        mPostTestServerAdapter = new RestAdapter.Builder()
                .setEndpoint("http://posttestserver.com")
                .setConverter(converter)
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

        final Countdown countdown = new Countdown();

        for (final String url : urls) {
            mPostTestServerAdapter.get(getEndpointFromUrl(url), new Callback<String>() {
                @Override
                public void success(final String s, final Response response) {
                    countdown.signal();
                }

                @Override
                public void failure(final RetrofitError error) {
                    countdown.signal();
                }
            });

            countdown.await();
        }

        countdown.blockUntilDone();
        mController.stop();

        return new ResponseStats(mController);
    }

    private static String getEndpointFromUrl(final String fullUrl) {
        return Uri.parse(fullUrl).getPath().substring(1);
    }

    private static class TypedOutputStream implements TypedOutput {

        private final String mFileName;
        private final String mMimeType;
        private final InputStream mStream;

        private TypedOutputStream(String fileName, String mimeType, InputStream stream) {
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

}

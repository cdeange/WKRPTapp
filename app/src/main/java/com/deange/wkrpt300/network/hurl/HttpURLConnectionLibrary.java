package com.deange.wkrpt300.network.hurl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.deange.wkrpt300.Utils;
import com.deange.wkrpt300.model.Countdown;
import com.deange.wkrpt300.model.OperationController;
import com.deange.wkrpt300.model.ResponseStats;
import com.deange.wkrpt300.network.NetworkLibrary;
import com.deange.wkrpt300.model.OperationParams;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;

public class HttpURLConnectionLibrary extends NetworkLibrary {

    private Context mContext;
    private final OperationController mController;

    public HttpURLConnectionLibrary(final Context context) {
        mContext = context;
        mController = new OperationController(context);
    }

    @Override
    public ResponseStats.Get get(final String url, final OperationParams.Get params)
            throws IOException {

        mController.reset();
        mController.start();

        final URL obj = new URL(url);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.connect();
        final InputStream in = con.getInputStream();
        final byte[] bytes = Utils.streamToByteArray(in);
        in.close();
        con.disconnect();

        final String response = new String(bytes, Charset.defaultCharset());

        mController.stop();

        return new ResponseStats.Get(mController, response);
    }

    @Override
    public ResponseStats.Post post(final String url, final OperationParams.Post params)
            throws IOException {

        mController.reset();
        mController.start();

        final URL obj = new URL(url);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setDoOutput(true);
        con.connect();

        final InputStream fileStream =
                mContext.getResources().openRawResource(params.rawResourceId);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        Utils.streamToStream(fileStream, out);
        out.flush();
        out.close();

        final InputStream in = con.getInputStream();
        final byte[] bytes = Utils.streamToByteArray(in);
        in.close();
        con.disconnect();

        final String response = new String(bytes, Charset.defaultCharset());

        mController.stop();

        return new ResponseStats.Post(mController, response);
    }

    @Override
    public ResponseStats.MultipartPost postMultipart(final String url,
                                                     final OperationParams.Multipart params)
            throws IOException {

        mController.reset();
        mController.start();

        final String twoHyphens = "--";
        final String lineEnd = "\r\n";
        final String boundary = UUID.randomUUID().toString().replace("-", "");

        final InputStream fileStream =
                mContext.getResources().openRawResource(params.rawResourceId);

        final URL obj = new URL(url);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        con.setRequestProperty("uploaded_file", params.fileName);
        con.connect();

        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(twoHyphens + boundary + lineEnd);

        // File part
        out.writeBytes("Content-Disposition: form-data; name=\"" + params.fileName + "\";"
                + "filename=\"" + params.fileName + "\"" + lineEnd);
        out.writeBytes(lineEnd);

        Utils.streamToStream(fileStream, out);

        out.writeBytes(lineEnd);
        out.writeBytes(twoHyphens + boundary + lineEnd);

        // Form part
        out.writeBytes(twoHyphens + boundary + lineEnd);
        out.writeBytes("Content-Type: text/plain" + lineEnd);
        out.writeBytes("Content-Disposition: form-data; name=\"" +
                String.valueOf(params.formField.first) + "\"" + lineEnd);
        out.writeBytes(lineEnd + String.valueOf(params.formField.second) + lineEnd);
        out.flush();
        out.close();

        final InputStream in = con.getInputStream();
        final byte[] bytes = Utils.streamToByteArray(in);
        in.close();
        con.disconnect();

        final String response = new String(bytes, Charset.defaultCharset());

        mController.stop();

        return new ResponseStats.MultipartPost(mController, response);
    }

    @Override
    public ResponseStats.ImageGet loadImage(final String url, final OperationParams.Image params)
            throws IOException {

        mController.reset();
        mController.start();

        final URL obj = new URL(url);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.connect();

        final InputStream in = con.getInputStream();
        final Bitmap response = BitmapFactory.decodeStream(in);
        con.disconnect();

        mController.stop();

        return new ResponseStats.ImageGet(mController, response);
    }

    @Override
    public ResponseStats batchGet(final String... urls) throws IOException {

        mController.reset();
        mController.start();
        mCountdown = new Countdown();

        for (final String url : urls) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final URL obj = new URL(url);
                        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                        con.connect();
                        final InputStream in = con.getInputStream();
                        Utils.streamToByteArray(in); // Read the stream
                        in.close();
                        con.disconnect();
                    } catch (final Exception ignored) {

                    } finally {
                        mCountdown.signal();
                    }
                }
            }).start();

            mCountdown.await();
        }

        mCountdown.blockUntilDone();
        mController.stop();

        return new ResponseStats(mController);
    }

}

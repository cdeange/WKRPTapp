package com.deange.wkrpt300.network;

import com.deange.wkrpt300.model.Countdown;
import com.deange.wkrpt300.model.OperationParams;
import com.deange.wkrpt300.model.ResponseStats;

import java.io.IOException;

public abstract class NetworkLibrary {

    public Countdown mCountdown;

    public abstract ResponseStats.Get get(final String url, final OperationParams.Get params)
            throws IOException;

    public abstract ResponseStats.Post post(final String url, final OperationParams.Post params)
            throws IOException;

    public abstract ResponseStats.MultipartPost postMultipart(final String url,
                                                     final OperationParams.Multipart params)
            throws IOException;

    public abstract ResponseStats.ImageGet loadImage(final String url, final OperationParams.Image params)
            throws IOException;

    public abstract ResponseStats batchGet(final String... urls)
            throws IOException;

}

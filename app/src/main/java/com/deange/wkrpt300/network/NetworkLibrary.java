package com.deange.wkrpt300.network;

import com.deange.wkrpt300.model.OperationParams;
import com.deange.wkrpt300.model.ResponseStats;

import java.io.IOException;

public interface NetworkLibrary {

    public static final boolean LOGDEBUG = false;

    public ResponseStats.Get get(final String url, final OperationParams.Get params)
            throws IOException;

    public ResponseStats.Post post(final String url, final OperationParams.Post params)
            throws IOException;

    public ResponseStats.MultipartPost postMultipart(final String url,
                                                     final OperationParams.Multipart params)
            throws IOException;

    public ResponseStats.ImageGet loadImage(final String url, final OperationParams.Image params)
            throws IOException;

    public ResponseStats batchGet(final String... urls)
            throws IOException;

}

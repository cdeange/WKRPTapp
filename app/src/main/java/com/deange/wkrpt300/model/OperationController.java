package com.deange.wkrpt300.model;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public final class OperationController {

    private static final String TAG = "OperationController";
    private final Instrumentation mInstrumentation;

    private long mTime1;
    private long mTime2;

    public OperationController(final Context context) {
        mInstrumentation = new Instrumentation();
    }

    public void start() {
        Log.v(TAG, "START");
        mInstrumentation.startAllocCounting();
        mTime1 = System.nanoTime();
    }

    public void stop() {
        mTime2 = System.nanoTime();
        mInstrumentation.stopAllocCounting();
        Log.v(TAG, "STOP");
    }

    public Bundle getAllocInfo() {
        return mInstrumentation.getAllocCounts();
    }

    public long getDurationNanos() {
        return mTime2 - mTime1;
    }

    public void reset() {
        System.gc();
        mTime1 = 0;
        mTime2 = 0;
    }
}

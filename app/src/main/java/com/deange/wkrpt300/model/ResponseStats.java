package com.deange.wkrpt300.model;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.deange.wkrpt300.Exporter;
import com.deange.wkrpt300.FancyWriter;
import com.deange.wkrpt300.network.LibraryRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResponseStats implements Comparable<ResponseStats> {

    private Bundle mAllocInfo;
    private long mNanoTime;
    private long mStartMillis;

    public ResponseStats(final Bundle allocInfo, final long nanoTime) {
        mAllocInfo = allocInfo;
        mNanoTime = nanoTime;
        mStartMillis = System.currentTimeMillis();
    }

    public ResponseStats(final OperationController controller) {
        mAllocInfo = controller.getAllocInfo();
        mNanoTime = controller.getDurationNanos();
        mStartMillis = System.currentTimeMillis();
    }

    public static ResponseStats average(final List<ResponseStats> statses) {
        return average(statses.toArray(new ResponseStats[statses.size()]));
    }

    public static ResponseStats average(final ResponseStats... statses) {

        // Take the 2nd and 3rd quartile to reduce likelihood of including outliers.
        // Outliers skew the results for temporary network issues
        final int len   = statses.length;
        final int start = (int) (len * (1 / (float) 4));
        final int end   = (int) (len * (3 / (float) 4));
        final int N     = end - start;

        if (N == 0) {
            return null;
        }

        long globalAllocCount = 0;
        long globalAllocSize  = 0;
        long globalFreedCount = 0;
        long globalFreedSize  = 0;
        long globalGcCount    = 0;
        long nanoDuration     = 0;

        Arrays.sort(statses);

        final List<ResponseStats> exportedList = new ArrayList<ResponseStats>();

        // The middle 50% in terms of response time
        for (int i = start; i < end; i++) {
            final ResponseStats stats = statses[i];
            globalAllocCount += stats.getAllocInfo().getLong("global_alloc_count");
            globalAllocSize  += stats.getAllocInfo().getLong("global_alloc_size");
            globalFreedCount += stats.getAllocInfo().getLong("global_freed_count");
            globalFreedSize  += stats.getAllocInfo().getLong("global_freed_size");
            globalGcCount    += stats.getAllocInfo().getLong("gc_invocation_count");
            nanoDuration     += stats.getNanoDuration();

            exportedList.add(stats);
        }

        Exporter.orderByStartTime(LibraryRunner.CURRENT_TYPE, exportedList);

        globalAllocCount /= N;
        globalAllocSize  /= N;
        globalFreedCount /= N;
        globalFreedSize  /= N;
        globalGcCount    /= N;
        nanoDuration     /= N;

        final Bundle results = new Bundle();
        results.putLong("global_alloc_count",  globalAllocCount);
        results.putLong("global_alloc_size",   globalAllocSize);
        results.putLong("global_freed_count",  globalFreedCount);
        results.putLong("global_freed_size",   globalFreedSize);
        results.putLong("gc_invocation_count", globalGcCount);

        return new ResponseStats(results, nanoDuration);
    }

    public long getStartMillis() {
        return mStartMillis;
    }

    public long getNanoDuration() {
        return mNanoTime;
    }

    public Bundle getAllocInfo() {
        return mAllocInfo;
    }

    public String getAllocInfoPretty() {
        final Bundle b = getAllocInfo();
        return ""
                + "Global Alloc Count = " + b.getLong("global_alloc_count")
                + '\n'
                + "Global Alloc Size  = " + b.getLong("global_alloc_size") / (float) 1000 + " KB"
                + '\n'
                + "Global Freed Count = " + b.getLong("global_freed_count")
                + '\n'
                + "Global Freed Size  = " + b.getLong("global_freed_size") / (float) 1000 + " KB"
                + '\n'
                + "Garbage Collected  = " + b.getLong("gc_invocation_count");
    }

    public FancyWriter getOutput() {
        return new FancyWriter()
                .printRow("-")
                .print("Memory Usage")
                .print(getAllocInfoPretty())
                .printVar("Connection Time", (mNanoTime / (float) 1000000) + " ms")
                .printRow("-");
    }

    @Override
    public int compareTo(final ResponseStats o) {
        return o == null ? 1 : Double.compare(mNanoTime, o.mNanoTime);
    }

    public static class StringResponseStats extends ResponseStats {

        public String response;

        public StringResponseStats(final OperationController controller, String response) {
            super(controller);
            this.response = response;
        }
    }

    public static final class Get extends StringResponseStats {
        public Get(final OperationController controller, String response) {
            super(controller, response);
        }
    }

    public static final class Post extends StringResponseStats {
        public Post(final OperationController controller, String response) {
            super(controller, response);
        }
    }

    public static final class MultipartPost extends StringResponseStats {
        public MultipartPost(final OperationController controller, String response) {
            super(controller, response);
        }
    }

    public static class ImageGet extends ResponseStats {

        public Bitmap response;

        public ImageGet(final OperationController controller, Bitmap response) {
            super(controller);
            this.response = response;
        }
    }
}

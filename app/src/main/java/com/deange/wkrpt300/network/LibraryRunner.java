package com.deange.wkrpt300.network;

import android.util.Pair;

import com.deange.wkrpt300.FancyWriter;
import com.deange.wkrpt300.R;
import com.deange.wkrpt300.model.Countdown;
import com.deange.wkrpt300.model.OperationParams;
import com.deange.wkrpt300.ui.OperationView;
import com.deange.wkrpt300.model.ResponseStats;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LibraryRunner {

    public static String CURRENT_TYPE = "";

    private static final int REQUEST_REPETITIONS = 100;

    public static final String RANDOM_TXT_FILENAME = "random.txt";
    public static final String MULTIPART_KEY = "content";
    private static final String RANDOM_CONTENT = "B84FA1D7A6CA47F095DEA36A2C87B1DC2BE27FE8535B409BCD19F1D3D8C2B11537C4F16945E3D3B4769F2A2BBA41DA4017467BED031126B068029D39EE70A9DD69A4B9B5FCEF7F66F40EE276EF6F93279037FC1BF19BE37F48BD52C15FF3925A3153088EFB6BCC051EAF12BBC6BC5BA550766598E5E099FAF69C01BFF4EC90270AC155BAAF160D61DFDC0E330761103C7BAD9984C41E8CE79B078E16B15CBDDED14E3CB198E5C8E2D89913202391F9303C42F671CFB10A0FCEBA102678CEA3995C076EA4715A5902EB4A30BC74206B1D084B3F290B850EE2C28DE185F1835A6889D3487007FC24DB66E4162DD9866F466A5F9963E22A08F5C70DF512C3BE8485FF5612376B1752DDCB893A72EAD9C994078CCE06F1703F23E5788F8A20BBE258C38A71966799D6CB55A6130AB1E05EFA187FAC5C344EF7D58C1472EE6B7C9734536B564B1D57BEE2D4387302E88CEAF8017F7E12F4C0546E2C87F32FC225307D3A559122C1543045A4D8D183C3299B3E6B487282A111EF351E4A5299AE5B0D3C36B6736BF5BCA14F533C8D77B1C0286751F6159AA715DDBEBBDCED13C23C7BC6FA8FE3D3F992F1043416FD5CE4D86CCB2D16ECDD01D6AC53205795CCE38C9832EE32E5CF63BD0B22C58F9F54B436FB2AFF81E988F7E5F4263F9DC39D2014FC2D3A05C2A412D5FABA8764BDF1036EBF6E50584646F89835ED621FE08DD1E53FA6";

    public static final String URL_GET = "http://posttestserver.com/files/2014/07/29/f_23.28.43309185761";
    public static final String URL_POST = "http://posttestserver.com/post.php?dir=deange";
    public static final String URL_MULTIPART = "http://posttestserver.com/post.php?dir=deange";
    public static final String URL_IMAGE = "http://deange.ca/android.png";
    public static final String[] URL_BATCH = new String[] {
            "http://posttestserver.com/files/2014/07/29/f_00.30.37594730131", // PDF
            "http://posttestserver.com/files/2014/07/29/f_23.28.43309185761", // JSON
            "http://deange.ca/android.png",                                   // IMAGE
            "http://deange.ca/",                                              // HTML
            "http://posttestserver.com/files/2014/07/29/f_00.30.37594730131", // PDF
            "http://posttestserver.com/files/2014/07/29/f_23.28.43309185761", // JSON
            "http://deange.ca/android.png",                                   // IMAGE
            "http://deange.ca/",                                              // HTML
    };

    private NetworkLibrary mLibrary;
    private PrintWriter mWriter = new PrintWriter(System.out);
    private boolean mCancelled;

    public LibraryRunner() {

    }

    public LibraryRunner(final NetworkLibrary library) {
        mLibrary = library;
    }

    public void setStream(final PrintStream stream) {
        mWriter = new PrintWriter(stream);
    }

    public void setWriter(final PrintWriter writer) {
        mWriter = writer;
    }

    public void run(final NetworkLibrary library, final int options) {
        mLibrary = library;
        run(options);
    }

    public void run() {
        run(OperationView.ALL);
    }

    public void run(final int options) {
        try {
            unsafeRun(options);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void cancel() {
        mCancelled = true;
        if (mLibrary != null && mLibrary.mCountdown != null) {
            final Countdown countdown = mLibrary.mCountdown;
            while (!countdown.isDone()) {
                countdown.signal();
            }
        }
    }

    private boolean has(final int options, final int flag) {
        return (options & flag) != 0;
    }

    private void unsafeRun(final int options) throws Exception {

        if (mLibrary == null) {
            return;
        }

        mCancelled = false;
        CURRENT_TYPE = mLibrary.getClass().getSimpleName();

        new FancyWriter().printRow("-").flush(mWriter);

        if (has(options, OperationView.GET)) {
            runSingleLibraryMethod("GET", new ResponseStatsProducer() {
                @Override
                public ResponseStats getStats() throws IOException {
                    return mLibrary.get(URL_GET, new OperationParams.Get());
                }
            });
        }

        if (has(options, OperationView.POST)) {
            runSingleLibraryMethod("POST", new ResponseStatsProducer() {
                @Override
                public ResponseStats getStats() throws IOException {
                    return mLibrary.post(URL_POST,
                            new OperationParams.Post(R.raw.random, RANDOM_TXT_FILENAME));
                }
            });
        }

        if (has(options, OperationView.MULTI)) {
            runSingleLibraryMethod("MULTIPART-POST", new ResponseStatsProducer() {
                @Override
                public ResponseStats getStats() throws IOException {
                    return mLibrary.postMultipart(URL_MULTIPART,
                            new OperationParams.Multipart(
                                    Pair.create(MULTIPART_KEY, RANDOM_CONTENT),
                                    RANDOM_TXT_FILENAME, R.raw.random));
                }
            });
        }

        if (has(options, OperationView.IMAGE)) {
            runSingleLibraryMethod("IMAGE-GET", new ResponseStatsProducer() {
                @Override
                public ResponseStats getStats() throws IOException {
                    return mLibrary.loadImage(URL_IMAGE,
                            new OperationParams.Image());
                }
            });
        }

        if (has(options, OperationView.BATCH)) {
            runSingleLibraryMethod("BATCH", new ResponseStatsProducer() {
                @Override
                public ResponseStats getStats() throws IOException {
                    return mLibrary.batchGet(URL_BATCH);
                }
            });
        }

        mCancelled = false;
    }

    private void runSingleLibraryMethod(final String methodType,
                                        final ResponseStatsProducer producer) throws IOException {

        int repetitions = 0;
        final List<ResponseStats> list = new ArrayList<ResponseStats>();
        for (int i = 0; i <= REQUEST_REPETITIONS; i++) {
            if (!mCancelled) {
                list.add(producer.getStats());
                repetitions = i;
            }
        }

        new FancyWriter()
                .printVar("TYPE", methodType)
                .printVar("REPETITIONS", repetitions)
                .flush(mWriter);

        final ResponseStats average = ResponseStats.average(list);
        if (average != null) {
            average.getOutput().flush(mWriter);
        }
    }

    private interface ResponseStatsProducer {
        public ResponseStats getStats() throws IOException;
    }

}

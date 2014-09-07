package com.deange.wkrpt300.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.deange.wkrpt300.CharArrayPrintWriter;
import com.deange.wkrpt300.R;
import com.deange.wkrpt300.network.LibraryRunner;
import com.deange.wkrpt300.network.NetworkLibrary;
import com.deange.wkrpt300.network.hurl.HttpURLConnectionLibrary;
import com.deange.wkrpt300.network.retrofit.RetrofitLibrary;
import com.deange.wkrpt300.network.volley.VolleyLibrary;

public class LibraryFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "lib";
    private static int TASK_INSTANCES_RUNNING = 0;

    private Button mButton;
    private TextView mResultsView;
    private OperationView mOptionsView;
    private NetworkTask mTask;

    public static LibraryFragment newInstance(final int section) {
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, section);
        fragment.setArguments(args);
        return fragment;
    }

    public LibraryFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_library, container, false);

        mButton = (Button) rootView.findViewById(R.id.lib_start_button);
        mOptionsView = (OperationView) rootView.findViewById(R.id.lib_options_group);
        mResultsView = (TextView) rootView.findViewById(R.id.test_results);

        mButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.lib_start_button) {
            if (mTask == null) {
                run();
            } else {
                cancel();
            }
        }
    }

    private void run() {
        mTask = new NetworkTask(getLibrary(), mOptionsView.getType());
        final boolean started = mTask.start();
        if (started) {
            mButton.setText(R.string.cancel_tests_button);
        }
    }

    private void cancel() {
        mTask.cancel();
    }

    private NetworkLibrary getLibrary() {
        final Context context = getActivity();
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 0:  return new HttpURLConnectionLibrary(context);
            case 1:  return new RetrofitLibrary(context);
            case 2:  return new VolleyLibrary(context);
            default: return null;
        }
    }

    private final class NetworkTask extends AsyncTask<Void, Void, String> {

        private final NetworkLibrary mLibrary;
        private final int mOptions;
        private final LibraryRunner mRunner;

        private NetworkTask(final NetworkLibrary library, final int options) {
            mLibrary = library;
            mOptions = options;
            mRunner = new LibraryRunner(mLibrary);
        }

        public boolean start() {
            if (TASK_INSTANCES_RUNNING != 0) {
                Log.w("NetworkTask",
                        "Please wait for other NetworkTasks to finish!", new Exception());
                return false;
            }

            TASK_INSTANCES_RUNNING++;
            execute();
            return true;
        }

        public void cancel() {
            mRunner.cancel();
        }

        @Override
        protected String doInBackground(final Void... params) {

            final CharArrayPrintWriter writer = new CharArrayPrintWriter();
            mRunner.setWriter(writer);
            mRunner.run(mOptions);

            return writer.toString();
        }

        @Override
        protected void onPostExecute(final String result) {
            mTask = null;

            TASK_INSTANCES_RUNNING--;
            mButton.setText(R.string.run_tests_button);
            mResultsView.setText(result);
        }
    }
}

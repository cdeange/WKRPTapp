package com.deange.wkrpt300.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.deange.wkrpt300.R;
import com.deange.wkrpt300.network.NetworkLibrary;
import com.deange.wkrpt300.network.NetworkTask;
import com.deange.wkrpt300.network.hurl.HttpURLConnectionLibrary;
import com.deange.wkrpt300.network.retrofit.RetrofitLibrary;
import com.deange.wkrpt300.network.volley.VolleyLibrary;

import java.util.Observable;
import java.util.Observer;

public class LibraryFragment extends Fragment
        implements View.OnClickListener, NetworkTask.OnNetworkTaskCompleteListener {

    private static final String ARG_SECTION_NUMBER = "lib";

    private Button mButton;
    private TextView mResultsView;
    private OperationView mOptionsView;
    private UIObserver mObserver = new UIObserver() {
        @Override
        public void updateOnMainThread(final Observable observable, final Object data) {
            updateButtonText();
        }
    };

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

        updateButtonText();
        mButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        NetworkTask.register(mObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        NetworkTask.unregister(mObserver);
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.lib_start_button) {
            if (NetworkTask.getInstance() == null) {
                run();
            } else {
                cancel();
            }
        }
    }

    private void updateButtonText() {
        if (NetworkTask.canAcquireNewTask()) {
            mButton.setText(R.string.run_tests_button);
        } else {
            mButton.setText(R.string.cancel_tests_button);
        }
    }

    private void run() {
        final NetworkTask task = NetworkTask.acquireNewTask(
                getLibrary(), this, mOptionsView.getType());

        if (task == null) {
            Log.w("NetworkTask",
                    "Please wait for other NetworkTask to finish!", new IllegalStateException());
            return;
        }

        updateButtonText();
        task.execute();
    }

    private void cancel() {
        NetworkTask.getInstance().cancel();
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

    @Override
    public void onPostComplete(final String output) {
        NetworkTask.releaseTask();
        updateButtonText();

        mResultsView.setText(output);
    }

    private static abstract class UIObserver implements Observer {

        private static final Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void update(final Observable observable, final Object data) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateOnMainThread(observable, data);
                }
            });
        }

        public abstract void updateOnMainThread(final Observable observable, final Object data);
    }

}

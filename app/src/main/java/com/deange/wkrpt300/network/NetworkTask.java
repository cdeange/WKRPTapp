package com.deange.wkrpt300.network;

import android.os.AsyncTask;

import com.deange.wkrpt300.CharArrayPrintWriter;

import java.util.Observable;
import java.util.Observer;

public final class NetworkTask extends AsyncTask<Void, Void, String> {

    private static final Observable sTaskObservable = new Result();
    private static int sInstances = 0;
    private static NetworkTask sInstance;

    private final int mOptions;
    private final LibraryRunner mRunner;
    private final OnNetworkTaskCompleteListener mListener;

    public static NetworkTask getInstance() {
        return sInstance;
    }

    public static NetworkTask acquireNewTask(final NetworkLibrary library,
                                             final OnNetworkTaskCompleteListener listener,
                                             final int options) {
        if (sInstance != null) {
            return sInstance;
        }

        sInstance = new NetworkTask(library, listener, options);
        return sInstance;
    }

    public static void releaseTask() {
        sInstance = null;
        System.gc();
    }

    private NetworkTask(final NetworkLibrary library,
                       final OnNetworkTaskCompleteListener listener, final int options) {
        mRunner = new LibraryRunner(library);
        mListener = listener;
        mOptions = options;
    }

    public static void register(final Observer listener) {
        sTaskObservable.addObserver(listener);
    }

    public static void unregister(final Observer listener) {
        sTaskObservable.deleteObserver(listener);
    }

    public void cancel() {
        mRunner.cancel();
    }

    @Override
    protected String doInBackground(final Void... params) {

        sInstances++;
        sTaskObservable.notifyObservers();

        final CharArrayPrintWriter writer = new CharArrayPrintWriter();
        mRunner.setWriter(writer);
        mRunner.run(mOptions);

        return writer.toString();
    }

    @Override
    protected void onPostExecute(final String result) {
        if (mListener != null) {
            mListener.onPostComplete(result);
        }

        sInstances--;
        sTaskObservable.notifyObservers();
    }

    public static boolean canAcquireNewTask() {
        return sInstances == 0 || sInstance == null;
    }

    public interface OnNetworkTaskCompleteListener {
        public void onPostComplete(final String output);
    }

    private static final class Result extends Observable {
        @Override
        public void notifyObservers(final Object data) {
            setChanged();
            super.notifyObservers(data);
        }
    }

}

package com.deange.wkrpt300.network.hurl;

import android.os.AsyncTask;

public abstract class SafeAsyncTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(final Void... params) {
        try {
            doInBackground();
        } catch (final Exception ignored) {

        }
        return null;
    }

    @Override
    protected final void onPostExecute(final Void aVoid) {
        try {
            onPostExecute();
        } catch (final Exception ignored) {

        }
    }

    protected abstract void doInBackground() throws Exception;

    protected abstract void onPostExecute() throws Exception;
}

package com.deange.wkrpt300.network.volley;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.deange.wkrpt300.model.Countdown;

public class CountdownListener<T>
        implements Response.Listener<T>, Response.ErrorListener {

    private final Countdown mCountdown;

    CountdownListener(final Countdown countdown) {
        mCountdown = countdown;
    }

    @Override
    public void onErrorResponse(final VolleyError error) {
        mCountdown.signal();
    }

    @Override
    public void onResponse(final T response) {
        mCountdown.signal();
    }
}

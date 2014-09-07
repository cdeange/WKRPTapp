package com.deange.wkrpt300.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.deange.wkrpt300.R;

public class OperationView extends RadioGroup {

    public static final int GET   = 0x01;
    public static final int POST  = 0x02;
    public static final int MULTI = 0x04;
    public static final int IMAGE = 0x08;
    public static final int BATCH = 0x10;
    public static final int ALL = GET | POST | MULTI | IMAGE | BATCH;

    public OperationView(final Context context) {
        super(context);
    }

    public OperationView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.operations_view, this);

        findViewById(R.id.radio_all).setTag(ALL);
        findViewById(R.id.radio_get).setTag(GET);
        findViewById(R.id.radio_post).setTag(POST);
        findViewById(R.id.radio_multipart).setTag(MULTI);
        findViewById(R.id.radio_image).setTag(IMAGE);
        findViewById(R.id.radio_batch).setTag(BATCH);

        check(R.id.radio_all);
    }

    public int getType() {
        for (int i = 0; i < getChildCount(); i++) {
            if (((RadioButton) getChildAt(i)).isChecked()) {
                return Integer.parseInt(String.valueOf(getChildAt(i).getTag()));
            }
        }

        return ALL;
    }

}

package com.thundersnacks.virtualpantry;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class HelpDialog extends DialogPreference {

    public HelpDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        persistBoolean(positiveResult);
    }

}
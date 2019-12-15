package org.mifos.mobilewallet.mifospay.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public class OtpDigitsWatcher implements TextWatcher {

    private EditText previous, current, next;

    public OtpDigitsWatcher(EditText previous, EditText current, EditText next) {
        this.previous = previous;
        this.current = current;
        this.next = next;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO
    }

    @Override
    public void afterTextChanged(Editable s) {
        int count = s.length();
        if (count == 1 && next != null) {
            next.requestFocus();
        } else if (count == 0 && previous != null) {
            previous.requestFocus();
        }
    }
}

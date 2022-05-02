package ir.accountbooklet.android.Listeners;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class TextChangeListener implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence sequence, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable sequence) {
        textChanged();
    }


    public abstract void textChanged();

}

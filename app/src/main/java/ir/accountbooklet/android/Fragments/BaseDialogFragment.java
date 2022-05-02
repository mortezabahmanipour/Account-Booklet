package ir.accountbooklet.android.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ir.accountbooklet.android.Listeners.FragmentNavigation;
import ir.accountbooklet.android.Utils.Theme;

/*
 * Created by morteza_bahmanipoor on 2018/4/2.
 */

public class BaseDialogFragment extends DialogFragment {
    protected Context context;
    protected FragmentNavigation mFragmentNavigation;
    private boolean destroy;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if(window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int color = Theme.getColor(Theme.key_action_bar_default);
                if (color == 0xffffffff) {
                    int flags = window.getDecorView().getSystemUiVisibility();
                    flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    window.getDecorView().setSystemUiVisibility(flags);
                }
            }
        }
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigation) {
            mFragmentNavigation = (FragmentNavigation) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy = true;
    }

    public boolean isDestroy() {
        return destroy;
    }
}
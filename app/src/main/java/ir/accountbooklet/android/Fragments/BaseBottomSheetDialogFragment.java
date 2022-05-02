package ir.accountbooklet.android.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import ir.accountbooklet.android.Listeners.FragmentNavigation;

/*
 * Created by morteza_bahmanipoor on 2018/4/2.
 */

public class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {
    protected Context context;
    protected FragmentNavigation mFragmentNavigation;
    protected BottomSheetBehavior bottomSheetBehavior;
    private boolean destroy;

    private BottomSheetBehavior.BottomSheetCallback bottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            BaseBottomSheetDialogFragment.this.onStateChanged(bottomSheet, newState);
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            BaseBottomSheetDialogFragment.this.onSlide(bottomSheet, slideOffset);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @NonNull
    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = onCreateViewButtonSheet(savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setContentView(view);
        if (view == null) {
            return dialog;
        }
        View layout = (View) view.getParent();
        layout.setBackgroundColor(Color.TRANSPARENT);
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) layout.getLayoutParams()).getBehavior();
        if (behavior instanceof BottomSheetBehavior) {
            bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setBottomSheetCallback(bottomSheetBehaviorCallback);
        }
        onViewCreatedButtonSheet(savedInstanceState, view);
        return dialog;
    }

    public View onCreateViewButtonSheet(Bundle savedInstanceState) {
        return null;
    }

    public void onViewCreatedButtonSheet(Bundle savedInstanceState, View view) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigation) {
            mFragmentNavigation = (FragmentNavigation) context;
        }
    }

    public void onStateChanged(@NonNull View bottomSheet, int newState) {

    }

    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

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
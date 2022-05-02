package ir.accountbooklet.android.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ir.accountbooklet.android.Customs.ImageCropper.CropImageView;
import ir.accountbooklet.android.Customs.SelectorImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.Theme;

public class ImageCrapperFragment extends BaseDialogFragment implements View.OnClickListener, CropImageView.OnCropImageCompleteListener {

  public static ImageCrapperFragment newInstance() {
    return new ImageCrapperFragment();
  }

  private ViewGroup baseLayout;
  private SelectorImageView ivBack;
  private SelectorImageView ivRotateRight;
  private SelectorImageView ivFlipHorizontal;
  private SelectorImageView ivFlipVertical;
  private SelectorImageView ivRest;
  private TextView tvApply;
  private CropImageView ivCrop;
  private ImageCrapperListener listener;
  private Bitmap bitmap;
  private boolean fixAspectRatio = true;

  public interface ImageCrapperListener {
    void onImageCropped(Bitmap bitmap);
  }

  public ImageCrapperFragment setImage(File bitmap, ImageCrapperListener cropListener) {
    return setImage(BitmapFactory.decodeFile(bitmap!= null ? bitmap.getPath() : ""), cropListener);
  }

  public ImageCrapperFragment setFixAspectRatio(boolean fixAspectRatio) {
    this.fixAspectRatio = fixAspectRatio;
    return this;
  }

  public ImageCrapperFragment setImage(Bitmap bitmap, ImageCrapperListener cropListener) {
    this.bitmap = bitmap;
    this.listener = cropListener;
    return this;
  }

  private void initializeViews(View view) {
    baseLayout = view.findViewById(R.id.baseLayout);
    ivBack = view.findViewById(R.id.ivBack);
    ivRotateRight = view.findViewById(R.id.ivRotateRight);
    ivFlipHorizontal = view.findViewById(R.id.ivFlipHorizontal);
    ivFlipVertical = view.findViewById(R.id.ivFlipVertical);
    ivRest = view.findViewById(R.id.ivRest);
    tvApply = view.findViewById(R.id.tvApply);
    ivCrop = view.findViewById(R.id.ivCrop);
  }

  private void initializeDefaults() {
    baseLayout.setPadding(0, AndroidUtilities.statusBarHeight, 0, AndroidUtilities.navigationBarHeight);
    ivCrop.setSnapRadius(AndroidUtilities.dp(5));
    ivCrop.setFixedAspectRatio(fixAspectRatio);
  }

  private void initializeListeners() {
    ivBack.setOnClickListener(this);
    ivRotateRight.setOnClickListener(this);
    ivFlipHorizontal.setOnClickListener(this);
    ivFlipVertical.setOnClickListener(this);
    ivRest.setOnClickListener(this);
    tvApply.setOnClickListener(this);
    ivCrop.setOnCropImageCompleteListener(this);
  }

  private void setInfo() {
    ivCrop.setImageBitmap(bitmap);
  }

  @Override
  public void onStart() {
    super.onStart();
    if (getDialog() != null && getDialog().getWindow() != null) {
      getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_image_crapper, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initializeViews(view);
    initializeDefaults();
    initializeListeners();
    setInfo();
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.ivBack:
        dismiss();
        break;
      case R.id.tvApply:
        ivCrop.getCroppedImageAsync(600, 600);
        break;
      case R.id.ivRotateRight:
        ivCrop.rotateImage(90);
        break;
      case R.id.ivFlipHorizontal:
        ivCrop.flipImageHorizontally();
        break;
      case R.id.ivFlipVertical:
        ivCrop.flipImageVertically();
        break;
      case R.id.ivRest:
        ivCrop.resetCropRect();
        break;
    }
  }

  @Override
  public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
    if (listener != null) {
      listener.onImageCropped(result.getBitmap());
    }
    dismiss();
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    final Window window = dialog.getWindow();
    if(window != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        int color = Theme.getColor(Theme.key_action_bar_default);
        if (color == 0xffffffff) {
          int flags = window.getDecorView().getSystemUiVisibility();
          flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
          window.getDecorView().setSystemUiVisibility(flags);
        }
      }
    }
    return dialog;
  }
}

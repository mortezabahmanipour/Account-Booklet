package ir.accountbooklet.android.Customs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import ir.accountbooklet.android.R;

public class ShadowView extends View {
  private Context context;
  private @ColorInt int start, center, end;
  private GradientDrawable.Orientation orientation;

  public ShadowView(Context context) {
    this(context, null);
  }

  public ShadowView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    initStyle(attrs, defStyleAttr);
    initialize();
  }

  private void initStyle(AttributeSet attrs, int defStyle) {
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShadowView, defStyle, 0);
    if (a == null) return;
    start = a.getColor(R.styleable.ShadowView_shv_start_color, ContextCompat.getColor(context, R.color.colorShadowCenter));
    center = a.getColor(R.styleable.ShadowView_shv_center_color, ContextCompat.getColor(context, R.color.colorShadowCenter));
    end = a.getColor(R.styleable.ShadowView_shv_end_color, ContextCompat.getColor(context, R.color.colorShadowCenter));
    orientation = a.getInt(R.styleable.ShadowView_shv_orientation, 0) == 0 ? GradientDrawable.Orientation.TOP_BOTTOM : GradientDrawable.Orientation.LEFT_RIGHT;
    a.recycle();
  }

  private void initialize() {
    GradientDrawable gradientDrawable = new GradientDrawable(orientation, new int[] {start, end});
    gradientDrawable.setCornerRadius(0f);
    setBackgroundDrawable(gradientDrawable);
  }
}

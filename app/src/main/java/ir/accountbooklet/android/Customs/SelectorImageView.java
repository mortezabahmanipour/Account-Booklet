package ir.accountbooklet.android.Customs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.Theme;

public class SelectorImageView extends AppCompatImageView {
  private Context context;
  private Theme.SelectorBuilder selectorBuilder;
  private int alpha = 255;
  private AnimatorSet animatorSet;
  private boolean isSquare;
  private boolean animate;
  private int tintColor;

  public SelectorImageView(Context context) {
    this(context, null);
  }

  public SelectorImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SelectorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    initStyle(attrs, defStyleAttr);
    initialize();
  }

  private void initStyle(AttributeSet attrs, int defStyleAttr) {
    selectorBuilder = Theme.initializeStyle(context, attrs, defStyleAttr);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectorImageView, defStyleAttr, 0);
      tintColor = a.getColor(R.styleable.SelectorImageView_siv_tint_color, 0);
      isSquare = a.getBoolean(R.styleable.SelectorImageView_siv_square, false);
      alpha = (int)(255 * a.getFloat(R.styleable.SelectorImageView_siv_alpha, 1f));
      a.recycle();
  }

  private void initialize() {
    Theme.setSelectorView(this, selectorBuilder);
  }

  public void setSquare(boolean square) {
    this.isSquare = square;
    restViews();
  }

  public void setTintColor(@ColorInt int tintColor) {
    this.tintColor = tintColor;
    restViews();
  }

  public void animating(boolean animate) {
    if (this.animate != animate) {
      this.animate = animate;

      if (animatorSet != null && animatorSet.isRunning()) {
        animatorSet.cancel();
      }

      animatorSet = new AnimatorSet();

      setAlpha(1f);

      if (!animate) {
        animatorSet = null;
        return;
      }

      animatorSet.play(ObjectAnimator.ofFloat(this, View.ALPHA, 0.1f, 1f))
        .after(ObjectAnimator.ofFloat(this, View.ALPHA,  1f, 0.1f));
      animatorSet.setDuration(1500);
      animatorSet.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          super.onAnimationEnd(animation);
          animatorSet.start();
        }
      });

      animatorSet.start();
    }
  }

  private void restViews() {
    requestLayout();
    invalidate();
  }

  private void setDrawableState() {
    if (getDrawable() == null) {
      return;
    }

    Drawable background = getDrawable().mutate();
    background.setAlpha(alpha);

    if (tintColor != 0) {
      Theme.setDrawableColor(background, tintColor);
    }

    setImageDrawable(background);
  }

  public Theme.SelectorBuilder getSelectorBuilder() {
    return selectorBuilder;
  }

  public void setSelectorBuilder(Theme.SelectorBuilder selectorBuilder) {
    this.selectorBuilder = selectorBuilder;

    Theme.setSelectorView(this, selectorBuilder);
  }

  @Override
  public void setImageResource(int resId) {
    super.setImageResource(resId);
    restViews();
  }

  @Override
  public void setImageDrawable(@Nullable Drawable drawable) {
    super.setImageDrawable(drawable);
    restViews();
  }

  @Override
  public void setImageBitmap(Bitmap bm) {
    super.setImageBitmap(bm);
    restViews();
  }

  @Override
  public void setImageURI(@Nullable Uri uri) {
    super.setImageURI(uri);
    restViews();
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    setDrawableState();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (isSquare) {
      int makeMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getMode(widthMeasureSpec));
      super.onMeasure(makeMeasureSpec, makeMeasureSpec);
    } else {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
  }
}

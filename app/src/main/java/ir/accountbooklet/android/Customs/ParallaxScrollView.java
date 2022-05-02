package ir.accountbooklet.android.Customs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.core.widget.NestedScrollView;
import ir.accountbooklet.android.R;

public class ParallaxScrollView extends NestedScrollView {

  private Context context;

  private boolean parallax = true;
  private int numberChildAt = 0;
  private ParallaxedView parallaxedView;

  public ParallaxScrollView(Context context) {
    this(context, null);
  }

  public ParallaxScrollView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ParallaxScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.context = context;
    initStyle(attrs, defStyle);
  }

  private void initStyle(AttributeSet attrs, int defStyleAttr) {
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ParallaxScrollView, defStyleAttr, 0);
    if (a != null) {
      if (a.hasValue(R.styleable.ParallaxScrollView_set_parallax)) {
        this.parallax = a.getBoolean(R.styleable.ParallaxScrollView_set_parallax, true);
      }
      if (a.hasValue(R.styleable.ParallaxScrollView_number_child_at_parallax)) {
        this.numberChildAt = a.getInt(R.styleable.ParallaxScrollView_number_child_at_parallax, 0);
      }
      a.recycle();
    }
  }

  public void setParallax(boolean parallax) {
    this.parallax = parallax;
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    makeViewsParallax();
  }

  private void makeViewsParallax() {
    if (getChildCount() > 0 && getChildAt(0) instanceof ViewGroup) {
      ViewGroup viewsHolder = (ViewGroup) getChildAt(0);
      if (viewsHolder.getChildCount() > 0 && viewsHolder.getChildAt(numberChildAt) != null) {
        parallaxedView = new ParallaxedView(viewsHolder.getChildAt(numberChildAt));
      }
    }
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if (!parallax) return;

    parallaxedView.setOffset((float)t / 1.9F);
    parallaxedView.animateNow();
  }

  private static class ParallaxedView {

    private WeakReference<View> view;
    private List<Animation> animations;

    private ParallaxedView(View view) {
      this.animations = new ArrayList<>();
      this.view = new WeakReference<>(view);
    }

    @SuppressLint("NewApi")
    private void setOffset(float offset) {
      View view = this.view.get();
      if (view != null) view.setTranslationY(offset);
    }

    public void setAlpha(float alpha) {
      View view = this.view.get();
      if (view != null) view.setAlpha(alpha);
    }

    private synchronized void animateNow() {
      View view = this.view.get();
      if (view != null) {
        AnimationSet set = new AnimationSet(true);
        for (Animation animation : animations) if (animation != null)
          set.addAnimation(animation);
        set.setDuration(0);
        set.setFillAfter(true);
        view.setAnimation(set);
        set.start();
        animations.clear();
      }
    }

    public void setView(View view) {
      this.view = new WeakReference<>(view);
    }
  }
}

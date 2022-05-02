package ir.accountbooklet.android.Customs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.HashMap;

import androidx.annotation.DrawableRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.AppLog;
import ir.accountbooklet.android.Utils.LayoutHelper;
import ir.accountbooklet.android.Utils.Theme;

public class ActionBarPopupWindow extends PopupWindow implements View.OnTouchListener, PopupWindow.OnDismissListener {
  private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

  public static class ListPopupView extends FrameLayout {
    private NestedScrollView scrollView;
    private LinearLayout linearLayout;
    private Drawable backgroundDrawable;
    private PopupItemClickListener popupItemClickListener;
    private AnimatorSet animatorSet;
    private HashMap<View, Integer> positions = new HashMap<>();
    private boolean showAnimation;
    private boolean mode2;
    private int positionSize;
    private float backAlpha = 1;
    private float backScaleY = 1;
    private float backScaleX = 1;
    private int popupWidth = AndroidUtilities.displaySize.x / 2;

    public interface PopupItemClickListener {
      void onPopupItemClicked(int action);
    }

    public ListPopupView(@NonNull Context context) {
      super(context);

      backgroundDrawable = getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();

      setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
      setWillNotDraw(false);

      try {
        scrollView = new NestedScrollView(context)  {
          @SuppressLint("ClickableViewAccessibility")
          @Override
          public boolean onTouchEvent(MotionEvent ev) {
            return !(animatorSet != null && animatorSet.isRunning()) && super.onTouchEvent(ev);
          }
        };
        scrollView.setVerticalScrollBarEnabled(false);
        addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
      } catch (Exception e) {
        AppLog.e(ActionBarPopupWindow.class, e.getMessage());
      }

      linearLayout = new LinearLayout(context);
      linearLayout.setOrientation(LinearLayout.VERTICAL);
      if (scrollView != null) {
        scrollView.addView(linearLayout, LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
      } else {
        addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
      }
    }

    public void setBackgroundDrawable(Drawable drawable) {
      this.backgroundDrawable = drawable;
    }

    public void setPopupItemClickListener(PopupItemClickListener popupItemClickListener) {
      this.popupItemClickListener = popupItemClickListener;
    }

    public void addPopupItem(int action, String str, @DrawableRes int res) {
      addPopupItem(action, str, getContext().getResources().getDrawable(res));
    }

    public void addPopupItem(int action, String str, Drawable icon) {
      final SubListLayout subListLayout = new SubListLayout(getContext(), str, icon);
      subListLayout.setTag(action);
      subListLayout.setOnClickListener(view -> {
        if (popupItemClickListener != null) {
          popupItemClickListener.onPopupItemClicked((Integer) subListLayout.getTag());
        }
      });
      linearLayout.addView(subListLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    public void addPopupItem(@NonNull View customView) {
      ViewGroup.LayoutParams params = customView.getLayoutParams();
      params.width = ViewGroup.LayoutParams.MATCH_PARENT;
      linearLayout.addView(customView, params);
    }

    @Keep
    public void setProgress(float progress) {
      if (mode2) {
        backAlpha = progress < 25 ? (progress * 4 / 100) : 1.0f;
        float scale = 0.98f + (progress <= 75 ? (progress / 1875f) : (0.04f - (progress - 75) / 1250));
        setScaleX(scale);
        setScaleY(scale);
//        setAlpha(progress < 25 ? (progress * 4 / 100) : 1.0f);
        invalidate();
      } else {
        backAlpha = progress / 100;
        backScaleY = progress / 100;

        int visiblePosition = (int) ((progress / 100) * positionSize);
        for (int i=0; i < linearLayout.getChildCount(); i++) {
          View view = linearLayout.getChildAt(showAnimation ? i : linearLayout.getChildCount() - 1 - i);
          Integer position = positions.get(view);
          if (position != null) {
            if (showAnimation && position + 1 <= visiblePosition) {
              setChildAnimation(view, true);
              positions.remove(view);
            } else if(!showAnimation && position >= visiblePosition) {
              setChildAnimation(view, false);
              positions.remove(view);
            }
          }
        }

        setAlpha(progress < 25 ? (progress * 4 / 100) : 1.0f);
        invalidate();
      }
    }

    public void clearItems() {
      linearLayout.removeAllViews();
    }

    public int getItemCount() {
      return linearLayout.getChildCount();
    }

    public int getPopupWidth() {
      return popupWidth;
    }

    public void showAnimation(final ActionBarPopupWindow actionBarPopupWindow, final boolean showAnimation, final boolean mode2) {
      if (animatorSet != null && animatorSet.isRunning()) {
        if (!this.showAnimation) {
          return;
        }
        animatorSet.removeAllListeners();
        animatorSet.cancel();
      }

      this.showAnimation = showAnimation;
      this.mode2 = mode2;
      if (scrollView != null && showAnimation) {
        scrollView.scrollTo(0, 0);
      }
      setAlpha(1.0f);
      animatorSet = new AnimatorSet();
      if (mode2 && showAnimation) {
        setTranslationY(0f);
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, "progress", 0, 100));
        animatorSet.setDuration(150);
        for (int i=0; i < linearLayout.getChildCount(); i++) {
          View view = linearLayout.getChildAt(i);
          if (view != null && view.getAlpha() != 1.0f) {
            view.setAlpha(1.0f);
          }
        }
      } else {
        animatorSet.setInterpolator(decelerateInterpolator);
        animatorSet.playTogether(
          showAnimation ? ObjectAnimator.ofFloat(this, "progress", 0, 100) : ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f) ,
          ObjectAnimator.ofFloat(this, "translationY", showAnimation ? AndroidUtilities.dp(-2.5f) : getTranslationY(), showAnimation ? 0 : AndroidUtilities.dp(-5f)));
        Rect scrollBounds = new Rect();
        if (scrollView != null) {
          scrollView.getHitRect(scrollBounds);
          scrollBounds.top = scrollBounds.top + scrollView.getScrollY();
          scrollBounds.bottom = scrollBounds.bottom + scrollView.getScrollY();
        } else {
          linearLayout.getHitRect(scrollBounds);
        }
        positions.clear();
        for (int i=0; i < linearLayout.getChildCount(); i++) {
          View view = linearLayout.getChildAt(i);
          if (showAnimation) {
            view.setAlpha(0.0f);
            view.setTranslationY(0.0f);
          }
          if (view.getTop() < scrollBounds.bottom && view.getBottom() > scrollBounds.top) {
            positions.put(view, positions.size());
          } else {
            if (!showAnimation) {
              view.setAlpha(0.0f);
            }
          }
        }
        positionSize = positions.size();
        animatorSet.setDuration(showAnimation ? 150 + positionSize * 16 : 250);
      }
      animatorSet.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          animatorSet = null;
          if (!showAnimation) {
            setFocusable(false);
            try {
              actionBarPopupWindow.dismiss(true);
            } catch (Exception e) {
              //don't prompt
            }
          } else {
            if (!mode2) {
              AndroidUtilities.runOnUIThread(() -> {
                for (int i=0; i < linearLayout.getChildCount(); i++) {
                  View view = linearLayout.getChildAt(i);
                  if (view != null && view.getAlpha() != 1.0f) {
                    view.setAlpha(1.0f);
                  }
                }
              }, 150);
            }
          }
        }
      });
      animatorSet.start();
    }

    private void setChildAnimation(View child, boolean showAnimation) {
      AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.playTogether(
        ObjectAnimator.ofFloat(child, "translationY", AndroidUtilities.dp(showAnimation ? -6 : 0), AndroidUtilities.dp(showAnimation ? 0 : -6)),
        ObjectAnimator.ofFloat(child, "alpha", showAnimation ? 0.0f : 1.0f, showAnimation ? 1.0f : 0.0f));
      animatorSet.setDuration(150);
      animatorSet.setInterpolator(decelerateInterpolator);
      animatorSet.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
      if (backgroundDrawable != null) {
        backgroundDrawable.setAlpha((int) (backAlpha * 255));
        backgroundDrawable.setBounds(0, 0, (int) (backScaleX * getMeasuredWidth()), (int) (backScaleY * getMeasuredHeight()) );
        backgroundDrawable.draw(canvas);
      }
    }

    private static class SubListLayout extends TextView {
      private SubListLayout(@NonNull Context context, String str, Drawable drawable) {
        super(context);
        setTextColor(getResources().getColor(R.color.colorBlackPrimary));
        setTextSize(12);
        setMaxLines(1);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.END);
        setTypefaceStyle(Typeface.BOLD);
        setPadding(AndroidUtilities.dp(15), 0, AndroidUtilities.dp(15), 0);
        if (drawable != null) {
          setDrawableSize(AndroidUtilities.dp(20));
          setCompoundDrawablePadding(AndroidUtilities.dp(10));
          setCompoundDrawables(null, null, Theme.setDrawableColor(drawable.mutate(), 0x50000000), null);
        }
        setGravity(Gravity.CENTER_VERTICAL);
        setText(str);
        setAlpha(0.0f);
        setBackground(Theme.createSelectorDrawable(new Theme.SelectorBuilder().pressedColor(Theme.getColor(Theme.key_app_selector))));
      }

      @Override
      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40), MeasureSpec.EXACTLY));
      }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      super.onMeasure(MeasureSpec.makeMeasureSpec(popupWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
    }
  }

//  private class NestedScrollView extends android.support.v4.widget.NestedScrollView {
//    public NestedScrollView(@NonNull Context context) {
//      super(context);
//    }
//
//    public void setLayoutSize(int size) {
//      int minHeight = popupItemSize + getShadowPadding();
//      int maxItemHeight = popupItems.size() * popupItemSize + getShadowPadding();
//      int startAlphaSize = maxItemHeight > maxBasePopupSize ? maxBasePopupSize : maxItemHeight - minHeight;
////
////      View view = (View) getParent();
////      view.setAlpha((((size - minHeight) * 100) / (float) startAlphaSize) / 100);
//      baseView.setBackAlpha((((size - minHeight) * 100) / (float) startAlphaSize) / 100);
//      baseView.setBackScaleY((((size - minHeight) * 100) / (float) startAlphaSize) / 100);
////
////      setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, size > minHeight ? size : minHeight));
//    }
//  }

  public ActionBarPopupWindow(View contentView) {
    super(contentView, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
    setOutsideTouchable(true);
    setClippingEnabled(true);
//    setTouchInterceptor(this);
//    setOnDismissListener(this);
//    setAnimationStyle(R.style.PopupAnimation);
    setInputMethodMode(INPUT_METHOD_NOT_NEEDED);
    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
    getContentView().setFocusableInTouchMode(true);
    getContentView().setOnKeyListener((view, keyCode, keyEvent) -> {
      if (keyCode == KeyEvent.KEYCODE_MENU && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == KeyEvent.ACTION_UP && isShowing()) {
        dismiss();
        return true;
      }
      return false;
    });
  }

  public void show(final View anchor, final boolean showOnAnchor) {
    show(anchor, showOnAnchor, false, new int[] {0, 0});
  }

  public void show(final View anchor, final boolean showOnAnchor, boolean mode2) {
    show(anchor, showOnAnchor, mode2, new int[] {0, 0});
  }

  public void show(final View anchor, final boolean showOnAnchor, boolean mode2, int[] position) {
    final View parent = getContentView();
    if (parent == null || isShowing()) {
      return;
    }
    setFocusable(true);
    if (anchor != null) {
      Rect rect = new Rect();
      anchor.getGlobalVisibleRect(rect);
      if (position.length == 2 && position[0] != 0 && position[1] != 0) {
        rect.set(position[0], rect.top + position[1] + (anchor.getTop() < 0 ? anchor.getTop() : 0), position[0], rect.top + position[1]);
      }
      int toastHufWidth = parent instanceof ListPopupView ? ((ListPopupView) parent).getPopupWidth() / 2 : 0;
      int xOff = (rect.right - rect.width() / 2) - toastHufWidth;
      int padding = AndroidUtilities.dp(10);
      int xOffset = Math.max(xOff, padding);
      xOffset = xOff + toastHufWidth * 2 > AndroidUtilities.displaySize.x - padding ? AndroidUtilities.displaySize.x - padding - toastHufWidth * 2 : xOffset;
      int yOffset = showOnAnchor ? rect.top : rect.bottom - AndroidUtilities.statusBarHeight / 2;
      showAtLocation(anchor, Gravity.TOP | 3, xOffset, yOffset);
    } else {
      showAtLocation(parent, Gravity.CENTER, 0, 0);
    }
    parent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        parent.getViewTreeObserver().removeOnPreDrawListener(this);
        checkPopupLayoutAnimation(true, mode2);
        return false;
      }
    });
  }

  private void checkPopupLayoutAnimation(boolean showAnimation, boolean mode2) {
    View contentView = getContentView();
    if (contentView == null) {
      ActionBarPopupWindow.super.dismiss();
      return;
    }
    if (contentView instanceof ListPopupView) {
      ((ListPopupView) contentView).showAnimation(this, showAnimation, mode2);
    } else {
      ActionBarPopupWindow.super.dismiss();
    }
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouch(View view, MotionEvent motionEvent) {
//    if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
//      dismiss();
//      return true;
//    }
    return false;
  }

  @Override
  public void onDismiss() {

  }

  public void dismiss(boolean dismiss) {
    if (dismiss) {
      super.dismiss();
    } else {
      dismiss();
    }
  }

  @Override
  public void dismiss() {
    checkPopupLayoutAnimation(false, false);
  }
}

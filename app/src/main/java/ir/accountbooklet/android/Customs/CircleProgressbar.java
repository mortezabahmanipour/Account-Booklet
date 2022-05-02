package ir.accountbooklet.android.Customs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Keep;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.Theme;

public class CircleProgressbar extends View {
  private Paint backPaint;
  private Paint forePaint;
  private RectF rectF = new RectF();
  private int backWidth;
  private int foreWidth;
  private int backColor;
  private int foreColor;
  private float progress = 0;
  private float startAngle = -90;
  private float sweepAngle = 0;
  private float maxProgress = 100;
  private long lastUpdateTime;
  private static final float rotationTime = 1800;
  private boolean clockWise;
  private boolean indeterminate;
  private ObjectAnimator objectAnimator;

  public CircleProgressbar(Context context) {
    this(context, null);
  }

  public CircleProgressbar(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CircleProgressbar(Context context, AttributeSet attrs, int i) {
    super(context, attrs, i);

    initializeStyledAttributes(attrs);

    backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    backPaint.setStyle(Paint.Style.STROKE);
    backPaint.setStrokeWidth(backWidth);
    backPaint.setColor(backColor);

    forePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    forePaint.setStyle(Paint.Style.STROKE);
    forePaint.setStrokeCap(Paint.Cap.ROUND);
    forePaint.setStrokeWidth(foreWidth);
    forePaint.setColor(foreColor);

    setProgress(progress);
    setClockwise(clockWise);
    setIndeterminate(true);
  }

  private void initializeStyledAttributes(AttributeSet attrs) {
    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressbar,0,0);
    backWidth = a.getDimensionPixelSize(R.styleable.CircleProgressbar_cpb_background_progress_width, AndroidUtilities.dp(2));
    foreWidth = a.getDimensionPixelSize(R.styleable.CircleProgressbar_cpb_foreground_progress_width, AndroidUtilities.dp(2));
    backColor = a.getColor(R.styleable.CircleProgressbar_cpb_background_progress_color, 0x20000000);
    foreColor = a.getColor(R.styleable.CircleProgressbar_cpb_foreground_progress_color, Theme.getColor(Theme.key_app_accent));
    progress = a.getInt(R.styleable.CircleProgressbar_cpb_progress, 0);
    clockWise = a.getBoolean(R.styleable.CircleProgressbar_cpb_clockwise, false);
    a.recycle();
  }

  public void setClockwise(boolean value) {
    clockWise = value;
    updateAngle();
  }

  public void setIndeterminate(boolean value) {
    if (indeterminate == value) {
      return;
    }
    indeterminate = value;
    if (indeterminate) {
      setProgress(progress == 0 ? 70 : progress);
    }
  }

  public void setBackWidth(int width) {
    backWidth = width;
    backPaint.setStrokeWidth(backWidth);
  }

  public void setForeWidth(int width) {
    foreWidth = width;
    forePaint.setStrokeWidth(foreWidth);
  }

  public void setBackColor(int color) {
    backColor = color;
    backPaint.setColor(color);
  }

  public void setForeColor(int color) {
    foreColor = color;
    forePaint.setColor(color);
  }

  public boolean isClockWise() {
    return clockWise;
  }

  public void setMaxProgress(int value) {
    maxProgress = value;
    setProgress(getProgress());
  }

  public boolean isIndeterminate() {
    return indeterminate;
  }

  public float getProgress() {
    return progress;
  }

  public float getMaxProgress() {
    return maxProgress;
  }

  @Keep
  public void setProgress(float progress) {
    this.progress = Math.min(progress, maxProgress);
    sweepAngle = (360 * progress / maxProgress);
    updateAngle();
  }

  private void updateAngle() {
    sweepAngle = Math.abs(sweepAngle);
    sweepAngle = clockWise ? -sweepAngle : sweepAngle;
  }

  private void updateAnimation() {
    if (indeterminate) {
      long newTime = System.currentTimeMillis();
      long dt = newTime - lastUpdateTime;
      if (dt > 17) {
        dt = 17;
      }
      lastUpdateTime = newTime;

      startAngle += 360 * dt / rotationTime;
      int count = (int) (startAngle / 360);
      startAngle -= count * 360;
    } else {
      lastUpdateTime = 0;
      startAngle = -90;
    }
    invalidate();
  }

  public void setProgressWithAnimation(float progress) {
    if (progress > maxProgress) {
      progress = maxProgress;
    }
    if (progress < 0) {
      progress = 0;
    }
    if (objectAnimator != null && objectAnimator.isRunning()) {
      objectAnimator.cancel();
    }
    objectAnimator = ObjectAnimator.ofFloat(this, "progress", getProgress(), progress);
    objectAnimator.setDuration(300);
    objectAnimator.setInterpolator(new DecelerateInterpolator());
    objectAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animator) {
        objectAnimator = null;
      }
    });
    objectAnimator.start();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    int size = Math.min(getWidth(), getHeight());
    float stroke = Math.max(backWidth, foreWidth) / 2f;
    rectF.set(stroke, stroke, size - stroke, size - stroke);
    canvas.drawArc(rectF, 0, 360, false, backPaint);
    canvas.drawArc(rectF, startAngle, sweepAngle, false, forePaint);
    updateAnimation();
  }
}
package ir.accountbooklet.android.Utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ReplacementSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

public class SpanUtil {

  public interface SpanClickListener {
    void onClick(View v, CharSequence str);
  }

  public static Spannable span(Spannable spannable, int start, int end, int textColor, float textSize, boolean bold) {
    spannable.setSpan(new CharacterStyle() {
      @Override
      public void updateDrawState(TextPaint tp) {
        tp.setColor(ColorUtils.setAlphaComponent(textColor, Color.alpha(tp.getColor())));
        if (textSize != -1) {
          tp.setTextSize(textSize);
        }
        tp.setFakeBoldText(bold);
      }
    }, start, end == -1 ? spannable.length() : end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    return spannable;
  }

  public static Spannable spanClick(Spannable spannable, int start, int end, int textColor, float textSize, boolean bold, SpanClickListener listener) {
    spannable.setSpan(new ClickableSpan() {
      @Override
      public void onClick(@NonNull View v) {
        if (listener != null) {
          listener.onClick(v, spannable.subSequence(start, end));
        }
      }

      @Override
      public void updateDrawState(@NonNull TextPaint tp) {
        tp.setColor(ColorUtils.setAlphaComponent(textColor, Color.alpha(tp.getColor())));
        if (textSize != -1) {
          tp.setTextSize(textSize);
        }
        tp.setFakeBoldText(bold);
      }
    }, start, end == -1 ? spannable.length() : end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    return spannable;
  }

  public static Spannable span(Spannable spannable, int start, int end, int textColor, float textSize, boolean bold, int backgroundColor, final float radii, int height, final Drawable drawable, int drawableSize, int drawablePadding, int leftPaddingText, int rightPaddingText, int leftPadding, int rightPadding) {
    if (drawable != null) {
      drawable.setBounds(0, 0, drawableSize, drawableSize);
    }
    spannable.setSpan(new ReplacementSpan() {
      @Override
      public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        if (textSize != -1) {
          paint.setTextSize(textSize);
        }
        paint.setFakeBoldText(bold);
        return (int) paint.measureText(text.subSequence(start, end).toString()) + leftPadding + rightPadding + leftPaddingText + rightPaddingText + (drawable != null ? drawablePadding + drawableSize : 0);
      }

      @Override
      public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        int size = getSize(paint, text, start, end, paint.getFontMetricsInt());
        paint.setColor(backgroundColor);
        RectF rectF = new RectF(x + leftPadding, top, (x + size) - rightPadding, height == -1 ? bottom : height < 0 ? bottom + height : top + height);
        float r = radii == -1 ? Math.min(rectF.height(), rectF.width()) / 2f : radii;
        canvas.drawRoundRect(rectF, r, r, paint);

        float needY = height > 0 && rectF.height() > 0 ? (rectF.bottom >= bottom ? (rectF.bottom - bottom) / 2f : (bottom - rectF.bottom) / -2f) : 0;
        needY *= 0;
        paint.setColor(textColor);
        canvas.drawText(text, start, end, x + leftPadding + leftPaddingText, y + needY, paint);

        if (drawable != null) {
          canvas.save();
          canvas.translate(rectF.right - drawableSize - rightPaddingText, top + ((bottom - top) / 2f) - (drawableSize / 2f) + needY);
          drawable.draw(canvas);
          canvas.restore();
        }
      }
    }, start, end == -1 ? spannable.length() : end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    return spannable;
  }

  public static void spanLine(Spannable spannable, int start, int end, int textColor, float textSize, boolean bold, int backgroundColor, final float radii, int height, int leftPaddingText, int rightPaddingText, int leftPadding, int rightPadding, int topPadding) {
    spannable.setSpan(new ReplacementSpan() {
      @Override
      public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        if (textSize != -1) {
          paint.setTextSize(textSize);
        }
        paint.setFakeBoldText(bold);
        return (int) paint.measureText(text.subSequence(start, end).toString()) + leftPadding + rightPadding + leftPaddingText + rightPaddingText;
      }

      @Override
      public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        int size = getSize(paint, text, start, end, paint.getFontMetricsInt());
        paint.setColor(backgroundColor);
        float topRect = (top + ((bottom - top) / 2f)) - (height / 2f) + topPadding;
        RectF rectF = new RectF(x + leftPadding, topRect, (x + size) - rightPadding, topRect + height);
        float r = radii == -1 ? Math.min(rectF.height(), rectF.width()) / 2f : radii;
        canvas.drawRoundRect(rectF, r, r, paint);

        paint.setColor(textColor);
        canvas.drawText(text, start, end, x + leftPadding + leftPaddingText, y, paint);
      }
    }, start, end == -1 ? spannable.length() : end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  }
}

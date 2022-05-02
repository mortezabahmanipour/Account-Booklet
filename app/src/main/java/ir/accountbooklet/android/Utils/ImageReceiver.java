package ir.accountbooklet.android.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

import java.util.Arrays;

public class ImageReceiver {

  private View parentView;
  private Paint roundPaint;
  private Paint borderPaint;
  private RectF roundRect = new RectF();
  private Path roundPath = new Path();
  private Matrix shaderMatrix = new Matrix();
  private BitmapShader shader;
  private Bitmap bitmap;
  private int imageX, imageY, imageW, imageH;
  private float[] radii = new float[8];
  private float alpha = 1.0f;
  private float borderRadius;

  public ImageReceiver(View view) {
    parentView = view;

    roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    borderPaint.setStyle(Paint.Style.STROKE);
  }

  private void createImageInfo() {
    if (bitmap == null || (bitmap.getWidth() <= 0 && bitmap.getHeight() <= 0)) {
      shader = null;
      return;
    }

    if (shader == null) {
      shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      roundPaint.setShader(shader);
    }
  }

  public boolean draw(Canvas canvas) {
    try {
      createImageInfo();

      if (shader != null) {
        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();
        float realImageW = imageW - (borderRadius * 2);
        float realImageH = imageH - (borderRadius * 2);
        float scale;
        float translateX = 0;
        float translateY = 0;
        if (bitmapW * realImageH > realImageW * bitmapH) {
          scale = realImageH / (float) bitmapH;
          translateX = (realImageW / scale - bitmapW) / 2f;
        } else {
          scale = realImageW / (float) bitmapW;
          translateY = (realImageH / scale - bitmapH) / 2f;
        }

        roundRect.set(imageX + borderRadius, imageY + borderRadius, imageX + realImageW + borderRadius, imageY + realImageH + borderRadius);

        shaderMatrix.reset();
        shaderMatrix.setScale(scale, scale);
        shaderMatrix.preTranslate(translateX, translateY);
        shader.setLocalMatrix(shaderMatrix);

        roundPaint.setAlpha((int) (alpha * 255));
        borderPaint.setAlpha((int) (alpha * 255));

        roundPath.reset();
        roundPath.addRoundRect(roundRect, radii, Path.Direction.CW);
        roundPath.close();
        if (borderRadius > 0) {
          canvas.drawPath(roundPath, borderPaint);
        }
        canvas.drawPath(roundPath, roundPaint);
        return true;
      }
    } catch (Exception e) {
      AppLog.e(ImageReceiver.class, e.getMessage());
    }
    return false;
  }

  public void clear() {
    shader = null;
    bitmap = null;
  }

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public void setImageX(int imageX) {
    this.imageX = imageX;
  }

  public void setImageY(int imageY) {
    this.imageY = imageY;
  }

  public void setImageW(int imageW) {
    this.imageW = imageW;
  }

  public void setImageH(int imageH) {
    this.imageH = imageH;
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  public void setBorderRadius(float borderRadius) {
    this.borderRadius = borderRadius;
    borderPaint.setStrokeWidth(borderRadius * 2);
  }

  public void setBorderColor(int color) {
    borderPaint.setColor(color);
  }

  public void setRadii(float... radii) {
    if (radii != null) {
      if (radii.length == 1) {
        Arrays.fill(this.radii, radii[0]);
      } else if (radii.length == 4) {
        this.radii = new float[] {radii[0], radii[0], radii[1], radii[1], radii[2], radii[2], radii[3], radii[3]};
      } else if (radii.length == 8) {
        this.radii = radii;
      }
    }
  }

  public float[] getRadii() {
    return radii;
  }
}

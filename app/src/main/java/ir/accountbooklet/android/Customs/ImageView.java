package ir.accountbooklet.android.Customs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.ImageReceiver;

public class ImageView extends AppCompatImageView {
  private Context context;
  private ImageReceiver imageReceiver = new ImageReceiver(this);
  private boolean square;
  private boolean circle;

  public ImageView(Context context) {
    this(context, null);
  }

  public ImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.context = context;
    initializeStyle(attrs, defStyle);
  }

  private void initializeStyle(AttributeSet attrs, int defStyle) {
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyle, 0);
    square = a.getBoolean(R.styleable.ImageView_shiv_square, square);
    circle = a.getBoolean(R.styleable.ImageView_shiv_circle, circle);
    imageReceiver.setBorderRadius(a.getDimension(R.styleable.ImageView_shiv_border_width, 0));
    imageReceiver.setBorderColor(a.getColor(R.styleable.ImageView_shiv_border_color, 0xFF000000));
    imageReceiver.setRadii(a.getDimension(R.styleable.ImageView_shiv_radius, 0));
    a.recycle();
    invalidate();
  }
  public void setRadius(float... radii) {
    imageReceiver.setRadii(radii);
    invalidate();
  }

  public final void onImageDrawableReset() {
    if (imageReceiver != null) {
      imageReceiver.clear();
    }
  }

  private Bitmap getBitmap() {
    Drawable drawable = getDrawable();
    if(drawable != null) {
      if(drawable instanceof BitmapDrawable) {
        return ((BitmapDrawable) drawable).getBitmap();
      }
      int width = drawable.getIntrinsicWidth();
      int height = drawable.getIntrinsicHeight();
      if (width > 0 && height  > 0) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
      }
    }
    return null;
  }

  private boolean hasRadii() {
    for (float radius : imageReceiver.getRadii()) {
      if (radius > 0) {
        return true;
      }
    }
    return false;
  }

  public void setBorderColor(int borderColor) {
    imageReceiver.setBorderColor(borderColor);
    invalidate();
  }

  public void setBorderWidth(int borderWidth) {
    imageReceiver.setBorderRadius(borderWidth);
    invalidate();
  }

  public void setCircle(boolean circle) {
    this.circle = circle;
    invalidate();
  }

  public void setSquare(boolean square) {
    this.square = square;
    invalidate();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (square) {
      int makeMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getMode(widthMeasureSpec));
      super.onMeasure(makeMeasureSpec, makeMeasureSpec);
    } else {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
  }

  @Override
  public void setImageBitmap(Bitmap bm) {
    super.setImageBitmap(bm);
    onImageDrawableReset();
  }

  @Override
  public void setImageDrawable(Drawable drawable) {
    super.setImageDrawable(drawable);
    onImageDrawableReset();
  }

  @Override
  public void setImageResource(int resId) {
    super.setImageResource(resId);
    onImageDrawableReset();
  }

  @Override
  public void setImageURI(@Nullable Uri uri) {
    super.setImageURI(uri);
    onImageDrawableReset();
  }

  @Override
  public void setBackground(Drawable background) {
    super.setBackground(background);
    onImageDrawableReset();
  }

  @Override
  public void setBackgroundDrawable(Drawable background) {
    super.setBackgroundDrawable(background);
    onImageDrawableReset();
  }

  @Override
  public void setBackgroundColor(int color) {
    super.setBackgroundColor(color);
    onImageDrawableReset();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    int width = getWidth();
    int height = getHeight();
    if (circle) {
      imageReceiver.setRadii(Math.max(width, height) / 2f);
    }
    if (hasRadii()) {
      imageReceiver.setImageW(width);
      imageReceiver.setImageH(height);
      if (imageReceiver.getBitmap() == null) {
        imageReceiver.setBitmap(getBitmap());
      }
      if (imageReceiver.draw(canvas)) {
        return;
      }
    }
    super.onDraw(canvas);
  }
}

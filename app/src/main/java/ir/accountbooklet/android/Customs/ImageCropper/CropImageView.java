package ir.accountbooklet.android.Customs.ImageCropper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;
import java.util.UUID;

import ir.accountbooklet.android.R;

public class CropImageView extends FrameLayout {

  private final ImageView mImageView;

  private final CropOverlayView mCropOverlayView;

  private final Matrix mImageMatrix = new Matrix();

  private final Matrix mImageInverseMatrix = new Matrix();

  private final ProgressBar mProgressBar;

  private final float[] mImagePoints = new float[8];

  private final float[] mScaleImagePoints = new float[8];

  private CropImageAnimation mAnimation;

  private Bitmap mBitmap;

  private int mDegreesRotated;

  private boolean mFlipHorizontally;

  private boolean mFlipVertically;

  private int mLayoutWidth;

  private int mLayoutHeight;

  private int mImageResource;

  private ScaleType mScaleType;

  private boolean mSaveBitmapToInstanceState = false;

  private boolean mShowCropOverlay = true;

  private boolean mShowProgressBar = true;

  private Uri mLoadedImageUri;

  private boolean mAutoZoomEnabled;

  private int mMaxZoom;

  private OnSetCropOverlayMovedListener mOnSetCropOverlayMovedListener;

  private OnSetCropWindowChangeListener mOnSetCropWindowChangeListener;

  private OnCropImageCompleteListener mOnCropImageCompleteListener;

  private int mLoadedSampleSize = 1;

  private float mZoom = 1;

  private float mZoomOffsetX;

  private float mZoomOffsetY;

  private RectF mRestoreCropWindowRect;

  private boolean mSizeChanged;

  private Uri mSaveInstanceStateBitmapUri;

  private WeakReference<BitmapCroppingWorkerTask> mBitmapCroppingWorkerTask;

  public CropImageView(Context context) {
    this(context, null);
  }

  public CropImageView(Context context, AttributeSet attrs) {
    super(context, attrs);

    CropImageOptions options = new CropImageOptions();

    if (attrs != null) {
      TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CropImageView, 0, 0);
      try {
        options.fixAspectRatio =
            ta.getBoolean(R.styleable.CropImageView_cropFixAspectRatio, options.fixAspectRatio);
        options.aspectRatioX =
            ta.getInteger(R.styleable.CropImageView_cropAspectRatioX, options.aspectRatioX);
        options.aspectRatioY =
            ta.getInteger(R.styleable.CropImageView_cropAspectRatioY, options.aspectRatioY);
        options.scaleType =
            ScaleType.values()[
                ta.getInt(R.styleable.CropImageView_cropScaleType, options.scaleType.ordinal())];
        options.autoZoomEnabled =
            ta.getBoolean(R.styleable.CropImageView_cropAutoZoomEnabled, options.autoZoomEnabled);
        options.multiTouchEnabled =
            ta.getBoolean(
                R.styleable.CropImageView_cropMultiTouchEnabled, options.multiTouchEnabled);
        options.maxZoom = ta.getInteger(R.styleable.CropImageView_cropMaxZoom, options.maxZoom);
        options.cropShape =
            CropShape.values()[
                ta.getInt(R.styleable.CropImageView_cropShape, options.cropShape.ordinal())];
        options.guidelines =
            Guidelines.values()[
                ta.getInt(
                    R.styleable.CropImageView_cropGuidelines, options.guidelines.ordinal())];
        options.snapRadius =
            ta.getDimension(R.styleable.CropImageView_cropSnapRadius, options.snapRadius);
        options.touchRadius =
            ta.getDimension(R.styleable.CropImageView_cropTouchRadius, options.touchRadius);
        options.initialCropWindowPaddingRatio =
            ta.getFloat(
                R.styleable.CropImageView_cropInitialCropWindowPaddingRatio,
                options.initialCropWindowPaddingRatio);
        options.borderLineThickness =
            ta.getDimension(
                R.styleable.CropImageView_cropBorderLineThickness, options.borderLineThickness);
        options.borderLineColor =
            ta.getInteger(R.styleable.CropImageView_cropBorderLineColor, options.borderLineColor);
        options.borderCornerThickness =
            ta.getDimension(
                R.styleable.CropImageView_cropBorderCornerThickness,
                options.borderCornerThickness);
        options.borderCornerOffset =
            ta.getDimension(
                R.styleable.CropImageView_cropBorderCornerOffset, options.borderCornerOffset);
        options.borderCornerLength =
            ta.getDimension(
                R.styleable.CropImageView_cropBorderCornerLength, options.borderCornerLength);
        options.borderCornerColor =
            ta.getInteger(
                R.styleable.CropImageView_cropBorderCornerColor, options.borderCornerColor);
        options.guidelinesThickness =
            ta.getDimension(
                R.styleable.CropImageView_cropGuidelinesThickness, options.guidelinesThickness);
        options.guidelinesColor =
            ta.getInteger(R.styleable.CropImageView_cropGuidelinesColor, options.guidelinesColor);
        options.backgroundColor =
            ta.getInteger(R.styleable.CropImageView_cropBackgroundColor, options.backgroundColor);
        options.showCropOverlay =
            ta.getBoolean(R.styleable.CropImageView_cropShowCropOverlay, mShowCropOverlay);
        options.showProgressBar =
            ta.getBoolean(R.styleable.CropImageView_cropShowProgressBar, mShowProgressBar);
        options.borderCornerThickness =
            ta.getDimension(
                R.styleable.CropImageView_cropBorderCornerThickness,
                options.borderCornerThickness);
        options.minCropWindowWidth =
            (int)
                ta.getDimension(
                    R.styleable.CropImageView_cropMinCropWindowWidth, options.minCropWindowWidth);
        options.minCropWindowHeight =
            (int)
                ta.getDimension(
                    R.styleable.CropImageView_cropMinCropWindowHeight,
                    options.minCropWindowHeight);
        options.minCropResultWidth =
            (int)
                ta.getFloat(
                    R.styleable.CropImageView_cropMinCropResultWidthPX,
                    options.minCropResultWidth);
        options.minCropResultHeight =
            (int)
                ta.getFloat(
                    R.styleable.CropImageView_cropMinCropResultHeightPX,
                    options.minCropResultHeight);
        options.maxCropResultWidth =
            (int)
                ta.getFloat(
                    R.styleable.CropImageView_cropMaxCropResultWidthPX,
                    options.maxCropResultWidth);
        options.maxCropResultHeight =
            (int)
                ta.getFloat(
                    R.styleable.CropImageView_cropMaxCropResultHeightPX,
                    options.maxCropResultHeight);
        options.flipHorizontally =
            ta.getBoolean(
                R.styleable.CropImageView_cropFlipHorizontally, options.flipHorizontally);
        options.flipVertically =
            ta.getBoolean(R.styleable.CropImageView_cropFlipHorizontally, options.flipVertically);

        mSaveBitmapToInstanceState =
            ta.getBoolean(
                R.styleable.CropImageView_cropSaveBitmapToInstanceState,
                mSaveBitmapToInstanceState);

        // if aspect ratio is set then set fixed to true
        if (ta.hasValue(R.styleable.CropImageView_cropAspectRatioX)
            && ta.hasValue(R.styleable.CropImageView_cropAspectRatioX)
            && !ta.hasValue(R.styleable.CropImageView_cropFixAspectRatio)) {
          options.fixAspectRatio = true;
        }
      } finally {
        ta.recycle();
      }
    }

    options.validate();

    mScaleType = options.scaleType;
    mAutoZoomEnabled = options.autoZoomEnabled;
    mMaxZoom = options.maxZoom;
    mShowCropOverlay = options.showCropOverlay;
    mShowProgressBar = options.showProgressBar;
    mFlipHorizontally = options.flipHorizontally;
    mFlipVertically = options.flipVertically;

    LayoutInflater inflater = LayoutInflater.from(context);
    View v = inflater.inflate(R.layout.crop_image_view, this, true);

    mImageView = v.findViewById(R.id.ImageView_image);
    mImageView.setScaleType(ImageView.ScaleType.MATRIX);

    mCropOverlayView = v.findViewById(R.id.CropOverlayView);
    mCropOverlayView.setCropWindowChangeListener(inProgress -> {
        handleCropWindowChanged(inProgress, true);
        OnSetCropOverlayMovedListener movedListener = mOnSetCropOverlayMovedListener;
        if (movedListener != null && inProgress) {
          movedListener.onCropOverlayMoved(getCropRect());
        }
      });
    mCropOverlayView.setInitialAttributeValues(options);

    mProgressBar = v.findViewById(R.id.CropProgressBar);
    setProgressBarVisibility();
  }

  /** Set auto-zoom functionality to enabled/disabled. */
  public void setAutoZoomEnabled(boolean autoZoomEnabled) {
    if (mAutoZoomEnabled != autoZoomEnabled) {
      mAutoZoomEnabled = autoZoomEnabled;
      handleCropWindowChanged(false, false);
      mCropOverlayView.invalidate();
    }
  }

  /** Set multi touch functionality to enabled/disabled. */
  public void setMultiTouchEnabled(boolean multiTouchEnabled) {
    if (mCropOverlayView.setMultiTouchEnabled(multiTouchEnabled)) {
      handleCropWindowChanged(false, false);
      mCropOverlayView.invalidate();
    }
  }

  /** The max zoom allowed during cropping. */
  public void setMaxZoom(int maxZoom) {
    if (mMaxZoom != maxZoom && maxZoom > 0) {
      mMaxZoom = maxZoom;
      handleCropWindowChanged(false, false);
      mCropOverlayView.invalidate();
    }
  }

  public void setMinCropResultSize(int minCropResultWidth, int minCropResultHeight) {
    mCropOverlayView.setMinCropResultSize(minCropResultWidth, minCropResultHeight);
  }

  public void setMaxCropResultSize(int maxCropResultWidth, int maxCropResultHeight) {
    mCropOverlayView.setMaxCropResultSize(maxCropResultWidth, maxCropResultHeight);
  }

  public int getRotatedDegrees() {
    return mDegreesRotated;
  }

  public void setSnapRadius(float snapRadius) {
    if (snapRadius >= 0) {
      mCropOverlayView.setSnapRadius(snapRadius);
    }
  }


  public Rect getWholeImageRect() {
    int loadedSampleSize = mLoadedSampleSize;
    Bitmap bitmap = mBitmap;
    if (bitmap == null) {
      return null;
    }

    int orgWidth = bitmap.getWidth() * loadedSampleSize;
    int orgHeight = bitmap.getHeight() * loadedSampleSize;
    return new Rect(0, 0, orgWidth, orgHeight);
  }

  public Rect getCropRect() {
    int loadedSampleSize = mLoadedSampleSize;
    Bitmap bitmap = mBitmap;
    if (bitmap == null) {
      return null;
    }

    // get the points of the crop rectangle adjusted to source bitmap
    float[] points = getCropPoints();

    int orgWidth = bitmap.getWidth() * loadedSampleSize;
    int orgHeight = bitmap.getHeight() * loadedSampleSize;

    // get the rectangle for the points (it may be larger than original if rotation is not stright)
    return BitmapUtils.getRectFromPoints(
        points,
        orgWidth,
        orgHeight,
        mCropOverlayView.isFixAspectRatio(),
        mCropOverlayView.getAspectRatioX(),
        mCropOverlayView.getAspectRatioY());
  }

  public float[] getCropPoints() {

    // Get crop window position relative to the displayed image.
    RectF cropWindowRect = mCropOverlayView.getCropWindowRect();

    float[] points =
        new float[] {
          cropWindowRect.left,
          cropWindowRect.top,
          cropWindowRect.right,
          cropWindowRect.top,
          cropWindowRect.right,
          cropWindowRect.bottom,
          cropWindowRect.left,
          cropWindowRect.bottom
        };

    mImageMatrix.invert(mImageInverseMatrix);
    mImageInverseMatrix.mapPoints(points);

    for (int i = 0; i < points.length; i++) {
      points[i] *= mLoadedSampleSize;
    }

    return points;
  }

  public void setCropRect(Rect rect) {
    mCropOverlayView.setInitialCropWindowRect(rect);
  }

  public void resetCropRect() {
    mZoom = 1;
    mZoomOffsetX = 0;
    mZoomOffsetY = 0;
    mDegreesRotated = 0;
    mFlipHorizontally = false;
    mFlipVertically = false;
    applyImageMatrix(getWidth(), getHeight(), false, false);
    mCropOverlayView.resetCropWindowRect();
  }

  public Bitmap getCroppedImage() {
    return getCroppedImage(0, 0, RequestSizeOptions.NONE);
  }

  public Bitmap getCroppedImage(int reqWidth, int reqHeight) {
    return getCroppedImage(reqWidth, reqHeight, RequestSizeOptions.RESIZE_INSIDE);
  }

  /**
   * Gets the cropped image based on the current crop window.<br>
   *
   * @param reqWidth the width to resize the cropped image to (see options)
   * @param reqHeight the height to resize the cropped image to (see options)
   * @param options the resize method to use, see its documentation
   * @return a new Bitmap representing the cropped image
   */
  public Bitmap getCroppedImage(int reqWidth, int reqHeight, RequestSizeOptions options) {
    Bitmap croppedBitmap = null;
    if (mBitmap != null) {
      mImageView.clearAnimation();

      reqWidth = options != RequestSizeOptions.NONE ? reqWidth : 0;
      reqHeight = options != RequestSizeOptions.NONE ? reqHeight : 0;

      if (mLoadedImageUri != null
          && (mLoadedSampleSize > 1 || options == RequestSizeOptions.SAMPLING)) {
        int orgWidth = mBitmap.getWidth() * mLoadedSampleSize;
        int orgHeight = mBitmap.getHeight() * mLoadedSampleSize;
        BitmapUtils.BitmapSampled bitmapSampled =
            BitmapUtils.cropBitmap(
                getContext(),
                mLoadedImageUri,
                getCropPoints(),
                mDegreesRotated,
                orgWidth,
                orgHeight,
                mCropOverlayView.isFixAspectRatio(),
                mCropOverlayView.getAspectRatioX(),
                mCropOverlayView.getAspectRatioY(),
                reqWidth,
                reqHeight,
                mFlipHorizontally,
                mFlipVertically);
        croppedBitmap = bitmapSampled.bitmap;
      } else {
        croppedBitmap =
            BitmapUtils.cropBitmapObjectHandleOOM(
                    mBitmap,
                    getCropPoints(),
                    mDegreesRotated,
                    mCropOverlayView.isFixAspectRatio(),
                    mCropOverlayView.getAspectRatioX(),
                    mCropOverlayView.getAspectRatioY(),
                    mFlipHorizontally,
                    mFlipVertically)
                .bitmap;
      }

      croppedBitmap = BitmapUtils.resizeBitmap(croppedBitmap, reqWidth, reqHeight, options);
    }

    return croppedBitmap;
  }

  public void setFixedAspectRatio(boolean fixAspectRatio) {
    mCropOverlayView.setFixedAspectRatio(fixAspectRatio);
  }

  public void getCroppedImageAsync() {
    getCroppedImageAsync(0, 0, RequestSizeOptions.NONE);
  }

  public void getCroppedImageAsync(int reqWidth, int reqHeight) {
    getCroppedImageAsync(reqWidth, reqHeight, RequestSizeOptions.RESIZE_INSIDE);
  }

  public void getCroppedImageAsync(int reqWidth, int reqHeight, RequestSizeOptions options) {
    if (mOnCropImageCompleteListener == null) {
      throw new IllegalArgumentException("mOnCropImageCompleteListener is not set");
    }
    startCropWorkerTask(reqWidth, reqHeight, options, null, null, 0);
  }

  public void setOnSetCropOverlayMovedListener(OnSetCropOverlayMovedListener listener) {
    mOnSetCropOverlayMovedListener = listener;
  }

  public void setOnCropWindowChangedListener(OnSetCropWindowChangeListener listener) {
    mOnSetCropWindowChangeListener = listener;
  }

  public void setOnCropImageCompleteListener(OnCropImageCompleteListener listener) {
    mOnCropImageCompleteListener = listener;
  }

  public void setImageBitmap(Bitmap bitmap) {
    mCropOverlayView.setInitialCropWindowRect(null);
    setBitmap(bitmap, 0, null, 1, 0);
  }

  public void clearImage() {
    clearImageInt();
    mCropOverlayView.setInitialCropWindowRect(null);
  }

  public void rotateImage(int degrees) {
    if (mBitmap != null) {
      // Force degrees to be a non-zero value between 0 and 360 (inclusive)
      if (degrees < 0) {
        degrees = (degrees % 360) + 360;
      } else {
        degrees = degrees % 360;
      }

      boolean flipAxes =
          !mCropOverlayView.isFixAspectRatio()
              && ((degrees > 45 && degrees < 135) || (degrees > 215 && degrees < 305));
      BitmapUtils.RECT.set(mCropOverlayView.getCropWindowRect());
      float halfWidth = (flipAxes ? BitmapUtils.RECT.height() : BitmapUtils.RECT.width()) / 2f;
      float halfHeight = (flipAxes ? BitmapUtils.RECT.width() : BitmapUtils.RECT.height()) / 2f;
      if (flipAxes) {
        boolean isFlippedHorizontally = mFlipHorizontally;
        mFlipHorizontally = mFlipVertically;
        mFlipVertically = isFlippedHorizontally;
      }

      mImageMatrix.invert(mImageInverseMatrix);

      BitmapUtils.POINTS[0] = BitmapUtils.RECT.centerX();
      BitmapUtils.POINTS[1] = BitmapUtils.RECT.centerY();
      BitmapUtils.POINTS[2] = 0;
      BitmapUtils.POINTS[3] = 0;
      BitmapUtils.POINTS[4] = 1;
      BitmapUtils.POINTS[5] = 0;
      mImageInverseMatrix.mapPoints(BitmapUtils.POINTS);

      // This is valid because degrees is not negative.
      mDegreesRotated = (mDegreesRotated + degrees) % 360;

      applyImageMatrix(getWidth(), getHeight(), true, false);

      // adjust the zoom so the crop window size remains the same even after image scale change
      mImageMatrix.mapPoints(BitmapUtils.POINTS2, BitmapUtils.POINTS);
      mZoom /=
          Math.sqrt(
              Math.pow(BitmapUtils.POINTS2[4] - BitmapUtils.POINTS2[2], 2)
                  + Math.pow(BitmapUtils.POINTS2[5] - BitmapUtils.POINTS2[3], 2));
      mZoom = Math.max(mZoom, 1);

      applyImageMatrix(getWidth(), getHeight(), true, false);

      mImageMatrix.mapPoints(BitmapUtils.POINTS2, BitmapUtils.POINTS);

      // adjust the width/height by the changes in scaling to the image
      double change =
          Math.sqrt(
              Math.pow(BitmapUtils.POINTS2[4] - BitmapUtils.POINTS2[2], 2)
                  + Math.pow(BitmapUtils.POINTS2[5] - BitmapUtils.POINTS2[3], 2));
      halfWidth *= change;
      halfHeight *= change;

      // calculate the new crop window rectangle to center in the same location and have proper
      // width/height
      BitmapUtils.RECT.set(
          BitmapUtils.POINTS2[0] - halfWidth,
          BitmapUtils.POINTS2[1] - halfHeight,
          BitmapUtils.POINTS2[0] + halfWidth,
          BitmapUtils.POINTS2[1] + halfHeight);

      mCropOverlayView.resetCropOverlayView();
      mCropOverlayView.setCropWindowRect(BitmapUtils.RECT);
      applyImageMatrix(getWidth(), getHeight(), true, false);
      handleCropWindowChanged(false, false);

      // make sure the crop window rectangle is within the cropping image bounds after all the
      // changes
      mCropOverlayView.fixCurrentCropWindowRect();
    }
  }

  public void flipImageHorizontally() {
    mFlipHorizontally = !mFlipHorizontally;
    applyImageMatrix(getWidth(), getHeight(), true, false);
  }

  public void flipImageVertically() {
    mFlipVertically = !mFlipVertically;
    applyImageMatrix(getWidth(), getHeight(), true, false);
  }

  void onImageCroppingAsyncComplete(BitmapCroppingWorkerTask.Result result) {
    mBitmapCroppingWorkerTask = null;
    setProgressBarVisibility();

    OnCropImageCompleteListener listener = mOnCropImageCompleteListener;
    if (listener != null) {
      CropResult cropResult =
          new CropResult(
              mBitmap,
              mLoadedImageUri,
              result.bitmap,
              result.uri,
              result.error,
              getCropPoints(),
              getCropRect(),
              getWholeImageRect(),
              getRotatedDegrees(),
              result.sampleSize);
      listener.onCropImageComplete(this, cropResult);
    }
  }

  private void setBitmap(Bitmap bitmap, int imageResource,
                         Uri imageUri, int loadSampleSize, int degreesRotated) {
    if (mBitmap == null || !mBitmap.equals(bitmap)) {

      mImageView.clearAnimation();

      clearImageInt();

      mBitmap = bitmap;
      mImageView.setImageBitmap(mBitmap);

      mLoadedImageUri = imageUri;
      mImageResource = imageResource;
      mLoadedSampleSize = loadSampleSize;
      mDegreesRotated = degreesRotated;

      applyImageMatrix(getWidth(), getHeight(), true, false);

      if (mCropOverlayView != null) {
        mCropOverlayView.resetCropOverlayView();
        setCropOverlayVisibility();
      }
    }
  }

  private void clearImageInt() {

    // if we allocated the bitmap, release it as fast as possible
    if (mBitmap != null && (mImageResource > 0 || mLoadedImageUri != null)) {
      mBitmap.recycle();
    }
    mBitmap = null;

    // clean the loaded image flags for new image
    mImageResource = 0;
    mLoadedImageUri = null;
    mLoadedSampleSize = 1;
    mDegreesRotated = 0;
    mZoom = 1;
    mZoomOffsetX = 0;
    mZoomOffsetY = 0;
    mImageMatrix.reset();
    mSaveInstanceStateBitmapUri = null;

    mImageView.setImageBitmap(null);

    setCropOverlayVisibility();
  }

  public void startCropWorkerTask(
      int reqWidth,
      int reqHeight,
      RequestSizeOptions options,
      Uri saveUri,
      Bitmap.CompressFormat saveCompressFormat,
      int saveCompressQuality) {
    Bitmap bitmap = mBitmap;
    if (bitmap != null) {
      mImageView.clearAnimation();

      BitmapCroppingWorkerTask currentTask =
          mBitmapCroppingWorkerTask != null ? mBitmapCroppingWorkerTask.get() : null;
      if (currentTask != null) {
        // cancel previous cropping
        currentTask.cancel(true);
      }

      reqWidth = options != RequestSizeOptions.NONE ? reqWidth : 0;
      reqHeight = options != RequestSizeOptions.NONE ? reqHeight : 0;

      int orgWidth = bitmap.getWidth() * mLoadedSampleSize;
      int orgHeight = bitmap.getHeight() * mLoadedSampleSize;
      if (mLoadedImageUri != null
          && (mLoadedSampleSize > 1 || options == RequestSizeOptions.SAMPLING)) {
        mBitmapCroppingWorkerTask =
            new WeakReference<>(
                new BitmapCroppingWorkerTask(
                    this,
                    mLoadedImageUri,
                    getCropPoints(),
                    mDegreesRotated,
                    orgWidth,
                    orgHeight,
                    mCropOverlayView.isFixAspectRatio(),
                    mCropOverlayView.getAspectRatioX(),
                    mCropOverlayView.getAspectRatioY(),
                    reqWidth,
                    reqHeight,
                    mFlipHorizontally,
                    mFlipVertically,
                    options,
                    saveUri,
                    saveCompressFormat,
                    saveCompressQuality));
      } else {
        mBitmapCroppingWorkerTask =
            new WeakReference<>(
                new BitmapCroppingWorkerTask(
                    this,
                    bitmap,
                    getCropPoints(),
                    mDegreesRotated,
                    mCropOverlayView.isFixAspectRatio(),
                    mCropOverlayView.getAspectRatioX(),
                    mCropOverlayView.getAspectRatioY(),
                    reqWidth,
                    reqHeight,
                    mFlipHorizontally,
                    mFlipVertically,
                    options,
                    saveUri,
                    saveCompressFormat,
                    saveCompressQuality));
      }
      mBitmapCroppingWorkerTask.get().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
      setProgressBarVisibility();
    }
  }

  @Override
  public Parcelable onSaveInstanceState() {
    if (mLoadedImageUri == null && mBitmap == null && mImageResource < 1) {
      return super.onSaveInstanceState();
    }

    Bundle bundle = new Bundle();
    Uri imageUri = mLoadedImageUri;
    if (mSaveBitmapToInstanceState && imageUri == null && mImageResource < 1) {
      mSaveInstanceStateBitmapUri =
          imageUri =
              BitmapUtils.writeTempStateStoreBitmap(
                  getContext(), mBitmap, mSaveInstanceStateBitmapUri);
    }
    if (imageUri != null && mBitmap != null) {
      String key = UUID.randomUUID().toString();
      BitmapUtils.mStateBitmap = new Pair<>(key, new WeakReference<>(mBitmap));
      bundle.putString("LOADED_IMAGE_STATE_BITMAP_KEY", key);
    }

    bundle.putParcelable("instanceState", super.onSaveInstanceState());
    bundle.putParcelable("LOADED_IMAGE_URI", imageUri);
    bundle.putInt("LOADED_IMAGE_RESOURCE", mImageResource);
    bundle.putInt("LOADED_SAMPLE_SIZE", mLoadedSampleSize);
    bundle.putInt("DEGREES_ROTATED", mDegreesRotated);
    bundle.putParcelable("INITIAL_CROP_RECT", mCropOverlayView.getInitialCropWindowRect());

    BitmapUtils.RECT.set(mCropOverlayView.getCropWindowRect());

    mImageMatrix.invert(mImageInverseMatrix);
    mImageInverseMatrix.mapRect(BitmapUtils.RECT);

    bundle.putParcelable("CROP_WINDOW_RECT", BitmapUtils.RECT);
    bundle.putString("CROP_SHAPE", mCropOverlayView.getCropShape().name());
    bundle.putBoolean("CROP_AUTO_ZOOM_ENABLED", mAutoZoomEnabled);
    bundle.putInt("CROP_MAX_ZOOM", mMaxZoom);
    bundle.putBoolean("CROP_FLIP_HORIZONTALLY", mFlipHorizontally);
    bundle.putBoolean("CROP_FLIP_VERTICALLY", mFlipVertically);

    return bundle;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    if (mBitmap != null) {

      // Bypasses a baffling bug when used within a ScrollView, where heightSize is set to 0.
      if (heightSize == 0) {
        heightSize = mBitmap.getHeight();
      }

      int desiredWidth;
      int desiredHeight;

      double viewToBitmapWidthRatio = Double.POSITIVE_INFINITY;
      double viewToBitmapHeightRatio = Double.POSITIVE_INFINITY;

      // Checks if either width or height needs to be fixed
      if (widthSize < mBitmap.getWidth()) {
        viewToBitmapWidthRatio = (double) widthSize / (double) mBitmap.getWidth();
      }
      if (heightSize < mBitmap.getHeight()) {
        viewToBitmapHeightRatio = (double) heightSize / (double) mBitmap.getHeight();
      }

      // If either needs to be fixed, choose smallest ratio and calculate from there
      if (viewToBitmapWidthRatio != Double.POSITIVE_INFINITY
          || viewToBitmapHeightRatio != Double.POSITIVE_INFINITY) {
        if (viewToBitmapWidthRatio <= viewToBitmapHeightRatio) {
          desiredWidth = widthSize;
          desiredHeight = (int) (mBitmap.getHeight() * viewToBitmapWidthRatio);
        } else {
          desiredHeight = heightSize;
          desiredWidth = (int) (mBitmap.getWidth() * viewToBitmapHeightRatio);
        }
      } else {
        // Otherwise, the picture is within frame layout bounds. Desired width is simply picture
        // size
        desiredWidth = mBitmap.getWidth();
        desiredHeight = mBitmap.getHeight();
      }

      int width = getOnMeasureSpec(widthMode, widthSize, desiredWidth);
      int height = getOnMeasureSpec(heightMode, heightSize, desiredHeight);

      mLayoutWidth = width;
      mLayoutHeight = height;

      setMeasuredDimension(mLayoutWidth, mLayoutHeight);

    } else {
      setMeasuredDimension(widthSize, heightSize);
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {

    super.onLayout(changed, l, t, r, b);

    if (mLayoutWidth > 0 && mLayoutHeight > 0) {
      // Gets original parameters, and creates the new parameters
      ViewGroup.LayoutParams origParams = this.getLayoutParams();
      origParams.width = mLayoutWidth;
      origParams.height = mLayoutHeight;
      setLayoutParams(origParams);

      if (mBitmap != null) {
        applyImageMatrix(r - l, b - t, true, false);

        // after state restore we want to restore the window crop, possible only after widget size
        // is known
        if (mRestoreCropWindowRect != null) {
          mImageMatrix.mapRect(mRestoreCropWindowRect);
          mCropOverlayView.setCropWindowRect(mRestoreCropWindowRect);
          handleCropWindowChanged(false, false);
          mCropOverlayView.fixCurrentCropWindowRect();
          mRestoreCropWindowRect = null;
        } else if (mSizeChanged) {
          mSizeChanged = false;
          handleCropWindowChanged(false, false);
        }
      } else {
        updateImageBounds(true);
      }
    } else {
      updateImageBounds(true);
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mSizeChanged = oldw > 0 && oldh > 0;
  }

  private void handleCropWindowChanged(boolean inProgress, boolean animate) {
    int width = getWidth();
    int height = getHeight();
    if (mBitmap != null && width > 0 && height > 0) {

      RectF cropRect = mCropOverlayView.getCropWindowRect();
      if (inProgress) {
        if (cropRect.left < 0
            || cropRect.top < 0
            || cropRect.right > width
            || cropRect.bottom > height) {
          applyImageMatrix(width, height, false, false);
        }
      } else if (mAutoZoomEnabled || mZoom > 1) {
        float newZoom = 0;
        // keep the cropping window covered area to 50%-65% of zoomed sub-area
        if (mZoom < mMaxZoom
            && cropRect.width() < width * 0.5f
            && cropRect.height() < height * 0.5f) {
          newZoom =
              Math.min(
                  mMaxZoom,
                  Math.min(
                      width / (cropRect.width() / mZoom / 0.64f),
                      height / (cropRect.height() / mZoom / 0.64f)));
        }
        if (mZoom > 1 && (cropRect.width() > width * 0.65f || cropRect.height() > height * 0.65f)) {
          newZoom =
              Math.max(
                  1,
                  Math.min(
                      width / (cropRect.width() / mZoom / 0.51f),
                      height / (cropRect.height() / mZoom / 0.51f)));
        }
        if (!mAutoZoomEnabled) {
          newZoom = 1;
        }

        if (newZoom > 0 && newZoom != mZoom) {
          if (animate) {
            if (mAnimation == null) {
              // lazy create animation single instance
              mAnimation = new CropImageAnimation(mImageView, mCropOverlayView);
            }
            // set the state for animation to start from
            mAnimation.setStartState(mImagePoints, mImageMatrix);
          }

          mZoom = newZoom;

          applyImageMatrix(width, height, true, animate);
        }
      }
      if (mOnSetCropWindowChangeListener != null && !inProgress) {
        mOnSetCropWindowChangeListener.onCropWindowChanged();
      }
    }
  }

  private void applyImageMatrix(float width, float height, boolean center, boolean animate) {
    if (mBitmap != null && width > 0 && height > 0) {

      mImageMatrix.invert(mImageInverseMatrix);
      RectF cropRect = mCropOverlayView.getCropWindowRect();
      mImageInverseMatrix.mapRect(cropRect);

      mImageMatrix.reset();

      // move the image to the center of the image view first so we can manipulate it from there
      mImageMatrix.postTranslate(
          (width - mBitmap.getWidth()) / 2, (height - mBitmap.getHeight()) / 2);
      mapImagePointsByImageMatrix();

      // rotate the image the required degrees from center of image
      if (mDegreesRotated > 0) {
        mImageMatrix.postRotate(
            mDegreesRotated,
            BitmapUtils.getRectCenterX(mImagePoints),
            BitmapUtils.getRectCenterY(mImagePoints));
        mapImagePointsByImageMatrix();
      }

      // scale the image to the image view, image rect transformed to know new width/height
      float scale =
          Math.min(
              width / BitmapUtils.getRectWidth(mImagePoints),
              height / BitmapUtils.getRectHeight(mImagePoints));
      if (mScaleType == ScaleType.FIT_CENTER
          || (mScaleType == ScaleType.CENTER_INSIDE && scale < 1)
          || (scale > 1 && mAutoZoomEnabled)) {
        mImageMatrix.postScale(
            scale,
            scale,
            BitmapUtils.getRectCenterX(mImagePoints),
            BitmapUtils.getRectCenterY(mImagePoints));
        mapImagePointsByImageMatrix();
      }

      // scale by the current zoom level
      float scaleX = mFlipHorizontally ? -mZoom : mZoom;
      float scaleY = mFlipVertically ? -mZoom : mZoom;
      mImageMatrix.postScale(
          scaleX,
          scaleY,
          BitmapUtils.getRectCenterX(mImagePoints),
          BitmapUtils.getRectCenterY(mImagePoints));
      mapImagePointsByImageMatrix();

      mImageMatrix.mapRect(cropRect);

      if (center) {
        // set the zoomed area to be as to the center of cropping window as possible
        mZoomOffsetX =
            width > BitmapUtils.getRectWidth(mImagePoints)
                ? 0
                : Math.max(
                        Math.min(
                            width / 2 - cropRect.centerX(), -BitmapUtils.getRectLeft(mImagePoints)),
                        getWidth() - BitmapUtils.getRectRight(mImagePoints))
                    / scaleX;
        mZoomOffsetY =
            height > BitmapUtils.getRectHeight(mImagePoints)
                ? 0
                : Math.max(
                        Math.min(
                            height / 2 - cropRect.centerY(), -BitmapUtils.getRectTop(mImagePoints)),
                        getHeight() - BitmapUtils.getRectBottom(mImagePoints))
                    / scaleY;
      } else {
        // adjust the zoomed area so the crop window rectangle will be inside the area in case it
        // was moved outside
        mZoomOffsetX =
            Math.min(Math.max(mZoomOffsetX * scaleX, -cropRect.left), -cropRect.right + width)
                / scaleX;
        mZoomOffsetY =
            Math.min(Math.max(mZoomOffsetY * scaleY, -cropRect.top), -cropRect.bottom + height)
                / scaleY;
      }

      // apply to zoom offset translate and update the crop rectangle to offset correctly
      mImageMatrix.postTranslate(mZoomOffsetX * scaleX, mZoomOffsetY * scaleY);
      cropRect.offset(mZoomOffsetX * scaleX, mZoomOffsetY * scaleY);
      mCropOverlayView.setCropWindowRect(cropRect);
      mapImagePointsByImageMatrix();
      mCropOverlayView.invalidate();

      // set matrix to apply
      if (animate) {
        // set the state for animation to end in, start animation now
        mAnimation.setEndState(mImagePoints, mImageMatrix);
        mImageView.startAnimation(mAnimation);
      } else {
        mImageView.setImageMatrix(mImageMatrix);
      }

      // update the image rectangle in the crop overlay
      updateImageBounds(false);
    }
  }

  /**
   * Adjust the given image rectangle by image transformation matrix to know the final rectangle of
   * the image.<br>
   * To get the proper rectangle it must be first reset to original image rectangle.
   */
  private void mapImagePointsByImageMatrix() {
    mImagePoints[0] = 0;
    mImagePoints[1] = 0;
    mImagePoints[2] = mBitmap.getWidth();
    mImagePoints[3] = 0;
    mImagePoints[4] = mBitmap.getWidth();
    mImagePoints[5] = mBitmap.getHeight();
    mImagePoints[6] = 0;
    mImagePoints[7] = mBitmap.getHeight();
    mImageMatrix.mapPoints(mImagePoints);
    mScaleImagePoints[0] = 0;
    mScaleImagePoints[1] = 0;
    mScaleImagePoints[2] = 100;
    mScaleImagePoints[3] = 0;
    mScaleImagePoints[4] = 100;
    mScaleImagePoints[5] = 100;
    mScaleImagePoints[6] = 0;
    mScaleImagePoints[7] = 100;
    mImageMatrix.mapPoints(mScaleImagePoints);
  }

  /**
   * Determines the specs for the onMeasure function. Calculates the width or height depending on
   * the mode.
   *
   * @param measureSpecMode The mode of the measured width or height.
   * @param measureSpecSize The size of the measured width or height.
   * @param desiredSize The desired size of the measured width or height.
   * @return The final size of the width or height.
   */
  private static int getOnMeasureSpec(int measureSpecMode, int measureSpecSize, int desiredSize) {

    // Measure Width
    int spec;
    if (measureSpecMode == MeasureSpec.EXACTLY) {
      // Must be this size
      spec = measureSpecSize;
    } else if (measureSpecMode == MeasureSpec.AT_MOST) {
      // Can't be bigger than...; match_parent value
      spec = Math.min(desiredSize, measureSpecSize);
    } else {
      // Be whatever you want; wrap_content
      spec = desiredSize;
    }

    return spec;
  }

  /**
   * Set visibility of crop overlay to hide it when there is no image or specificly set by client.
   */
  private void setCropOverlayVisibility() {
    if (mCropOverlayView != null) {
      mCropOverlayView.setVisibility(mShowCropOverlay && mBitmap != null ? VISIBLE : INVISIBLE);
    }
  }

  /**
   * Set visibility of progress bar when async loading/cropping is in process and show is enabled.
   */
  private void setProgressBarVisibility() {
    boolean visible =
        mShowProgressBar &&
          (mBitmap == null && mBitmapCroppingWorkerTask != null);
    mProgressBar.setVisibility(visible ? VISIBLE : INVISIBLE);
  }

  /** Update the scale factor between the actual image bitmap and the shown image.<br> */
  private void updateImageBounds(boolean clear) {
    if (mBitmap != null && !clear) {

      // Get the scale factor between the actual Bitmap dimensions and the displayed dimensions for
      // width/height.
      float scaleFactorWidth =
          100f * mLoadedSampleSize / BitmapUtils.getRectWidth(mScaleImagePoints);
      float scaleFactorHeight =
          100f * mLoadedSampleSize / BitmapUtils.getRectHeight(mScaleImagePoints);
      mCropOverlayView.setCropWindowLimits(
          getWidth(), getHeight(), scaleFactorWidth, scaleFactorHeight);
    }

    // set the bitmap rectangle and update the crop window after scale factor is set
    mCropOverlayView.setBounds(clear ? null : mImagePoints, getWidth(), getHeight());
  }

  public enum CropShape {
    RECTANGLE,
    OVAL
  }

  public enum ScaleType {

    FIT_CENTER,

    CENTER,
    CENTER_CROP,

    CENTER_INSIDE
  }

  public enum Guidelines {
    OFF,
    ON_TOUCH,
    ON
  }

  public enum RequestSizeOptions {
    NONE,
    SAMPLING,
    RESIZE_INSIDE,
    RESIZE_FIT,
    RESIZE_EXACT
  }

  public interface OnSetCropOverlayMovedListener {
    void onCropOverlayMoved(Rect rect);
  }

  public interface OnSetCropWindowChangeListener {
    void onCropWindowChanged();
  }

  public interface OnCropImageCompleteListener {
    void onCropImageComplete(CropImageView view, CropResult result);
  }

  public static class CropResult {
    private final Bitmap mOriginalBitmap;
    private final Uri mOriginalUri;
    private final Bitmap mBitmap;
    private final Uri mUri;
    private final Exception mError;
    private final float[] mCropPoints;
    private final Rect mCropRect;
    private final Rect mWholeImageRect;
    private final int mRotation;
    private final int mSampleSize;

    CropResult(
        Bitmap originalBitmap,
        Uri originalUri,
        Bitmap bitmap,
        Uri uri,
        Exception error,
        float[] cropPoints,
        Rect cropRect,
        Rect wholeImageRect,
        int rotation,
        int sampleSize) {
      mOriginalBitmap = originalBitmap;
      mOriginalUri = originalUri;
      mBitmap = bitmap;
      mUri = uri;
      mError = error;
      mCropPoints = cropPoints;
      mCropRect = cropRect;
      mWholeImageRect = wholeImageRect;
      mRotation = rotation;
      mSampleSize = sampleSize;
    }

    public Bitmap getOriginalBitmap() {
      return mOriginalBitmap;
    }

    public Uri getOriginalUri() {
      return mOriginalUri;
    }

    public boolean isSuccessful() {
      return mError == null;
    }

    public Bitmap getBitmap() {
      return mBitmap;
    }

    public Uri getUri() {
      return mUri;
    }

    public Exception getError() {
      return mError;
    }

    public float[] getCropPoints() {
      return mCropPoints;
    }

    public Rect getCropRect() {
      return mCropRect;
    }

    public Rect getWholeImageRect() {
      return mWholeImageRect;
    }

    public int getRotation() {
      return mRotation;
    }

    public int getSampleSize() {
      return mSampleSize;
    }
  }
}

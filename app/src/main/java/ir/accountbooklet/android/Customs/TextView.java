package ir.accountbooklet.android.Customs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.Theme;

import static android.text.TextUtils.TruncateAt.MARQUEE;

public class TextView extends AppCompatTextView {
    protected Context context;
    private AnimatorSet animatorSet;
    private Theme.SelectorBuilder selectorBuilder;
    private boolean isSquare;
    private boolean isMoving;
    private boolean isMany;
    private int drawableSize;
    private float drawableAlpha = 1.0f;
    private int drawableTintColor;
    private int typefaceStyle;

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initializeStyle(attrs, defStyle);
        initialize();
    }

    private void initializeStyle(AttributeSet attrs, int defStyleAttr) {
        selectorBuilder = Theme.initializeStyle(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView, defStyleAttr, 0);
        isMoving = a.getBoolean(R.styleable.TextView_tv_moving, false);
        isMany = a.getBoolean(R.styleable.TextView_tv_many, false);
        typefaceStyle = a.getInt(R.styleable.TextView_tv_face_style, Typeface.NORMAL);
        drawableTintColor = a.getColor(R.styleable.TextView_tv_drawable_tint_color, 0);
        drawableSize = a.getDimensionPixelSize(R.styleable.TextView_tv_drawable_size, 0);
        drawableAlpha = a.getFloat(R.styleable.TextView_tv_drawable_alpha, 1);
        isSquare = a.getBoolean(R.styleable.TextView_tv_square, false);
        a.recycle();
    }

    protected void initialize() {
        setIncludeFontPadding(false);
        setTypeface(AndroidUtilities.IRANSans_FaNum, typefaceStyle);
        setDrawableSize();
        if (selectorBuilder != null) {
            setBackground(Theme.createSelectorDrawable(selectorBuilder));
        }
        if (isMoving) {
            setMoving();
        }
        setText(getText());
    }

    private void restViews() {
        requestLayout();
        invalidate();
    }

    private void setDrawableSize() {
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    private Drawable getDrawableResize(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        drawable = drawable.mutate();
        drawable.setAlpha((int) (drawableAlpha * 255));
        if (drawableSize > 0) {
            drawable.setBounds(0, 0, drawableSize, drawableSize);
        }
        if (drawableTintColor != 0) {
            Theme.setDrawableColor(drawable, drawableTintColor);
        }
        return drawable;
    }

    public Theme.SelectorBuilder getSelectorBuilder() {
        return selectorBuilder;
    }

    public void setSelectorBuilder(Theme.SelectorBuilder selectorBuilder) {
        this.selectorBuilder = selectorBuilder;
        setBackground(Theme.createSelectorDrawable(selectorBuilder));
    }

    public void setMoving() {
        setEllipsize(MARQUEE);
        setMarqueeRepeatLimit(-1);
        setSingleLine(true);
        if (!isSelected()) {
            setSelected(true);
        }
    }

    public int getTypefaceStyle() {
        return typefaceStyle;
    }

    public void setSquare(boolean square) {
        this.isSquare = square;
        restViews();
    }

    public void setTypefaceStyle(int faceStyle) {
        this.typefaceStyle = faceStyle;
        setTypeface(getTypeface(), faceStyle);
    }

    @Keep
    public void setDrawableTintColor(@ColorInt int drawableTintColor) {
        this.drawableTintColor = drawableTintColor;
        setDrawableSize();
    }

    public void hide() {
        if (getVisibility() == View.INVISIBLE) {
            return;
        }
        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.cancel();
        }
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f, 0.0f));
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.INVISIBLE);
                animatorSet = null;
            }
        });
        animatorSet.start();
    }

    public void show() {
        if (getVisibility() == View.VISIBLE) {
            return;
        }
        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.cancel();
        }
        setVisibility(View.VISIBLE);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f, 1.0f));
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet = null;
            }
        });
        animatorSet.start();
    }

    public void show(boolean show) {
        if (show) {
            show();
        } else {
            hide();
        }
    }

    public void setDrawableSize(int drawableSize) {
        this.drawableSize = drawableSize;
        setDrawableSize();
    }

    public void setDrawableAlpha(float alpha) {
        this.drawableAlpha = alpha;
        setDrawableSize();
    }

    public void setMany(boolean many) {
        isMany = many;
        setText(getText());
    }

    public int getDrawableSize() {
        return drawableSize;
    }

    public int getWidthTextView() {
        return getWidthTextView(getText().toString());
    }

    public int getWidthTextView(String str) {
        Drawable[] drawables = getCompoundDrawables();
        return (int) getPaint().measureText(str) + (drawables[0] != null ? drawableSize + getCompoundDrawablePadding() : 0) + (drawables[2] != null ? drawableSize + getCompoundDrawablePadding() : 0) + getPaddingLeft() + getPaddingRight();
    }

    public int getHeightTextView() {
        Drawable[] drawables = getCompoundDrawables();
        int lineCount = getLineCount();
        return (int) (getTextSize() * lineCount) + (int) (getLineSpacingExtra() * (lineCount - 1)) + (drawables[1] != null ? drawableSize + getCompoundDrawablePadding() : 0) + (drawables[2] != null ? drawableSize + getCompoundDrawablePadding() : 0) + getPaddingTop() + getPaddingBottom();
    }

    @Override
    public void setText(CharSequence cs, BufferType type) {
        if (cs == null || !isMany) {
            super.setText(cs, type);
            return;
        }
        StringBuilder result = new StringBuilder();
        String text = cs.toString().replace(" ","").replace(",","");
        for (int i=0; i < text.length(); i++) {
            if ( i > 0 && i % 3 == 0) {
                result.insert(0, ",");
            }
            result.insert(0, text.charAt(text.length() - 1 - i));
        }
        super.setText(result.toString(), type);
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        super.setCompoundDrawables(getDrawableResize(left), getDrawableResize(top), getDrawableResize(right), getDrawableResize(bottom));
    }

    public void setPadding(int value) {
        super.setPadding(value, value, value, value);
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
